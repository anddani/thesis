#include "FFTIterativeNeon.h"

void* newTable1D(int len, int sizeOneElement)
{

    // Declare the table pointer
    char* tablePtr = NULL;

    // Declare the memory size
    int sizeTable = 0;

    // Padding
    char padding = 0;

    // Round up the size of the table such
    // that it can fit an alignment to 16 bytes
    sizeTable = sizeOneElement * len + 16;

    // Allocate memory
    tablePtr = (char *) malloc(sizeTable);

    // Compute the padding required
    padding = (char) (16 - (((size_t) tablePtr) & 0x0000000F));

    *((char*) (tablePtr + padding - 1)) = padding;

    // Return the pointer to the beginning of the table
    return ((void*) (tablePtr + padding));

}

void deleteTable1D(void* tablePtr)
{

    // Padding
    char padding;

    // Beginning of the allocated memory
    void* allocatedMemory;

    // Get the padding
    padding = *(((char*) tablePtr) - 1);

    // Get the pointer
    allocatedMemory = (void*) (((char*) tablePtr) - padding);

    // Free
    free(allocatedMemory);

}

void fftIterativeNeonInit(struct objFFT* myFFT, struct ParametersStruct* myParameters, unsigned int size) {
    // Temporary variable
    unsigned int tmpFrameSize;

    // Temporary variable
    unsigned int tmpNumberLevels;

    // Define the index to generate Wn(r)
    unsigned int r;

    // Define the index to generate the reverse bit order array
    unsigned int indexRevBitOrder;

    // Define the index to generate the empty array
    unsigned int emptyIndex;

    // Define the INDEX of the input parameter a
    unsigned int a;
    // Define the INDEX of the input parameter b
    unsigned int b;

    // Define accumulator to compute the index of parameters a and b
    unsigned int accumulatorA;
    // Define accumulator to compute the index of parameter r
    unsigned int accumulatorR;

    // Define the nubmer of groups in the current level
    unsigned int numberGroups;
    // Define the number of points per group
    unsigned int numberSubGroups;

    // Define the index of the level
    unsigned int indexLevel;
    // Define the index of the group
    unsigned int indexGroup;
    // Define the index of the point inside the group
    unsigned int indexSubGroup;

    // Define the index of the twiddle-factor in memory
    unsigned int indexTwiddle;

    // Define the index of the simd array for a with groups
    unsigned int simdAIndexGroup;
    // Define the index of the simd array for b with groups
    unsigned int simdBIndexGroup;
    // Define the index of the simd array for r with groups
    unsigned int simdRIndexGroup;
    // Define the index of the simd array for a with individual elements
    unsigned int simdAIndexIndividual;
    // Define the index of the simd array for b with individual elements
    unsigned int simdBIndexIndividual;

    // *************************************************************************
    // * STEP 1: Load parameters                                               *
    // *************************************************************************

    myFFT->FFT_SIZE = size;

    tmpFrameSize = myFFT->FFT_SIZE;
    tmpNumberLevels = 0;

    while(tmpFrameSize > 1)
    {
        tmpNumberLevels++;
        tmpFrameSize /= 2;
    }

    myFFT->FFT_NBLEVELS = tmpNumberLevels;
    myFFT->FFT_HALFSIZE = myFFT->FFT_SIZE / 2;
    myFFT->FFT_SIZE_INV = (1.0f / myFFT->FFT_SIZE);
    myFFT->FFT_SIMD_GROUP = ((myFFT->FFT_SIZE/2) * (myFFT->FFT_NBLEVELS-2) / 4);
    myFFT->FFT_SIMD_INDIVIDUAL = ((myFFT->FFT_SIZE/2) * 2);

    // *************************************************************************
    // * STEP 2: Initialize context                                            *
    // *************************************************************************

    // +-------------------------------------------------------------------+
    // | Step A: Create arrays                                             |
    // +-------------------------------------------------------------------+

    myFFT->WnReal = (float*) newTable1D(myFFT->FFT_HALFSIZE, sizeof(float));
    myFFT->WnImag = (float*) newTable1D(myFFT->FFT_HALFSIZE, sizeof(float));
    myFFT->simdWnReal = (float*) newTable1D(myFFT->FFT_SIMD_GROUP*4, sizeof(float));
    myFFT->simdWnImag = (float*) newTable1D(myFFT->FFT_SIMD_GROUP*4, sizeof(float));
    myFFT->workingArrayReal = (float*) newTable1D(myFFT->FFT_SIZE, sizeof(float));
    myFFT->workingArrayImag = (float*) newTable1D(myFFT->FFT_SIZE, sizeof(float));
    myFFT->fftTwiceReal = (float*) newTable1D(myFFT->FFT_SIZE, sizeof(float));
    myFFT->fftTwiceRealFlipped = (float*) newTable1D(myFFT->FFT_SIZE, sizeof(float));
    myFFT->fftTwiceImag = (float*) newTable1D(myFFT->FFT_SIZE, sizeof(float));
    myFFT->fftTwiceImagFlipped = (float*) newTable1D(myFFT->FFT_SIZE, sizeof(float));
    myFFT->emptyArray = (float*) newTable1D(myFFT->FFT_SIZE, sizeof(float));
    myFFT->trashArray = (float*) newTable1D(myFFT->FFT_SIZE, sizeof(float));
    myFFT->revBitOrderArray = (unsigned int*) newTable1D(myFFT->FFT_SIZE, sizeof(unsigned int));
    myFFT->simdARealGroups = (float**) newTable1D(myFFT->FFT_SIMD_GROUP, sizeof(float*));
    myFFT->simdAImagGroups = (float**) newTable1D(myFFT->FFT_SIMD_GROUP, sizeof(float*));
    myFFT->simdBRealGroups = (float**) newTable1D(myFFT->FFT_SIMD_GROUP, sizeof(float*));
    myFFT->simdBImagGroups = (float**) newTable1D(myFFT->FFT_SIMD_GROUP, sizeof(float*));
    myFFT->simdRRealGroups = (float**) newTable1D(myFFT->FFT_SIMD_GROUP, sizeof(float*));
    myFFT->simdRImagGroups = (float**) newTable1D(myFFT->FFT_SIMD_GROUP, sizeof(float*));
    myFFT->simdAIndividual = (float**) newTable1D(myFFT->FFT_SIMD_INDIVIDUAL, sizeof(float*));
    myFFT->simdBIndividual = (float**) newTable1D(myFFT->FFT_SIMD_INDIVIDUAL, sizeof(float*));

    // +-------------------------------------------------------------------+
    // | Step B: Generate the FFT factors Wn(r)                            |
    // +-------------------------------------------------------------------+

    // Generate Wn(r) = exp(-j*2*pi*r/N) for r = 0 ... (N/2 - 1)
    for (r = 0; r < myFFT->FFT_HALFSIZE; r++)
    {

        myFFT->WnReal[r] = cosf(2.0f * M_PI * r / myFFT->FFT_SIZE);
        myFFT->WnImag[r] = -1.0f * sinf(2.0f * M_PI * r / myFFT->FFT_SIZE);

    }

    // +-------------------------------------------------------------------+
    // | Step C: Generate an array with reverse-bit indexes                |
    // +-------------------------------------------------------------------+

    // Generate an array of reverse bit order
    for (indexRevBitOrder = 0; indexRevBitOrder < myFFT->FFT_SIZE; indexRevBitOrder++)
    {

        myFFT->revBitOrderArray[indexRevBitOrder] = (indexRevBitOrder & 0x0001) << 15;
        myFFT->revBitOrderArray[indexRevBitOrder] += (indexRevBitOrder & 0x0002) << 13;
        myFFT->revBitOrderArray[indexRevBitOrder] += (indexRevBitOrder & 0x0004) << 11;
        myFFT->revBitOrderArray[indexRevBitOrder] += (indexRevBitOrder & 0x0008) << 9;
        myFFT->revBitOrderArray[indexRevBitOrder] += (indexRevBitOrder & 0x0010) << 7;
        myFFT->revBitOrderArray[indexRevBitOrder] += (indexRevBitOrder & 0x0020) << 5;
        myFFT->revBitOrderArray[indexRevBitOrder] += (indexRevBitOrder & 0x0040) << 3;
        myFFT->revBitOrderArray[indexRevBitOrder] += (indexRevBitOrder & 0x0080) << 1;
        myFFT->revBitOrderArray[indexRevBitOrder] += (indexRevBitOrder & 0x0100) >> 1;
        myFFT->revBitOrderArray[indexRevBitOrder] += (indexRevBitOrder & 0x0200) >> 3;
        myFFT->revBitOrderArray[indexRevBitOrder] += (indexRevBitOrder & 0x0400) >> 5;
        myFFT->revBitOrderArray[indexRevBitOrder] += (indexRevBitOrder & 0x0800) >> 7;
        myFFT->revBitOrderArray[indexRevBitOrder] += (indexRevBitOrder & 0x1000) >> 9;
        myFFT->revBitOrderArray[indexRevBitOrder] += (indexRevBitOrder & 0x2000) >> 11;
        myFFT->revBitOrderArray[indexRevBitOrder] += (indexRevBitOrder & 0x4000) >> 13;
        myFFT->revBitOrderArray[indexRevBitOrder] += (indexRevBitOrder & 0x8000) >> 15;

        myFFT->revBitOrderArray[indexRevBitOrder] >>= (16 - myFFT->FFT_NBLEVELS);

    }

    // +-------------------------------------------------------------------+
    // | Step D: Generate an empty array                                   |
    // +-------------------------------------------------------------------+

    // Generate an empty array (will be used as a dummy array for the imaginary
    // part when the FFT of a single real signal is computed)
    for (emptyIndex = 0; emptyIndex < myFFT->FFT_SIZE; emptyIndex++)
    {
        myFFT->emptyArray[emptyIndex] = 0;
    }

    // +-------------------------------------------------------------------+
    // | Step E: SIMD: Compute the indexes used for memory accesses        |
    // +-------------------------------------------------------------------+

    // Load parameters
    numberGroups = 1;
    numberSubGroups = myFFT->FFT_HALFSIZE;

    // Initialize pointers
    simdAIndexGroup = 0;
    simdBIndexGroup = 0;
    simdRIndexGroup = 0;
    simdAIndexIndividual = 0;
    simdBIndexIndividual = 0;
    indexTwiddle = 0;

    // Loop for each level
    for (indexLevel = 0; indexLevel < myFFT->FFT_NBLEVELS; indexLevel++)
    {

        accumulatorA = 0;
        accumulatorR = 0;

        // Loop for each group in the current level
        for (indexGroup = 0; indexGroup < numberGroups; indexGroup++)
        {

            // Loop for each element of the group
            for (indexSubGroup = 0; indexSubGroup < numberSubGroups; indexSubGroup++)
            {

                // Calculate the indexes
                a = accumulatorA;
                b = accumulatorA + numberSubGroups;
                r = accumulatorR;
                accumulatorA++;
                accumulatorR += numberGroups;

                // Check if there are groups of at least 4 elements
                if (numberSubGroups >= 4)
                {

                    // Copy corresponding twiddle factor
                    myFFT->simdWnReal[indexTwiddle] = myFFT->WnReal[r];
                    myFFT->simdWnImag[indexTwiddle] = myFFT->WnImag[r];
                    indexTwiddle++;

                    // Check if a is a multiple of 4
                    if ((a / 4.0f) == floorf(a/4.0f))
                    {

                        myFFT->simdARealGroups[simdAIndexGroup] = &myFFT->workingArrayReal[a];
                        myFFT->simdAImagGroups[simdAIndexGroup] = &myFFT->workingArrayImag[a];
                        myFFT->simdBRealGroups[simdBIndexGroup] = &myFFT->workingArrayReal[b];
                        myFFT->simdBImagGroups[simdBIndexGroup] = &myFFT->workingArrayImag[b];
                        myFFT->simdRRealGroups[simdRIndexGroup] = &myFFT->simdWnReal[indexTwiddle - 1];
                        myFFT->simdRImagGroups[simdRIndexGroup] = &myFFT->simdWnImag[indexTwiddle - 1];

                        simdAIndexGroup++;
                        simdBIndexGroup++;
                        simdRIndexGroup++;

                    }

                }
                else
                {

                    myFFT->simdAIndividual[simdAIndexIndividual++] = &myFFT->workingArrayReal[a];
                    myFFT->simdBIndividual[simdBIndexIndividual++] = &myFFT->workingArrayReal[b];

                }

            }

            // Update accumulators
            accumulatorA += numberSubGroups;
            accumulatorR = 0;

        }

        // Divide the number of points by group by 2 for the next level
        numberSubGroups >>= 1;
        // Multiply the number of groups by 2 for the next level
        numberGroups <<= 1;

    }

}

void fftIterativeNeon(struct objFFT* myFFT, float* sourceArrayReal, float* sourceArrayImag, float* destArrayReal, float* destArrayImag) {

    // Array index
    unsigned int indexGroup;
    unsigned int indexLevel;
    unsigned int indexArray;

    // Define variables for the last two levels
    float valueAReal;
    float valueAImag;
    float valueBReal;
    float valueBImag;
    float newValueAReal;
    float newValueAImag;
    float newValueBReal;
    float newValueBImag;
    unsigned int a;
    unsigned int b;
    unsigned int accumulatorA;

    // Define the index to generate the reverse bit order array
    unsigned int indexRevBitOrder;

    // SIMD registers
    __m128_mod regA, regB, regC, regD, regE, regF, regG;

    // *************************************************************************
    // * STEP 0: Copy source                                                   *
    // *************************************************************************

    // Copy all elements from the source array in the working array
    for (indexArray = 0; indexArray < myFFT->FFT_SIZE; indexArray+=4)
    {

        // Load sourceArrayReal[k] in regA
        regA.m128 = vld1q_f32(&sourceArrayReal[indexArray]);

        // Load sourceArrayImag[k] in regB
        regB.m128 = vld1q_f32(&sourceArrayImag[indexArray]);

        // Copy regA in workingArrayReal[k]
        vst1q_f32(&myFFT->workingArrayReal[indexArray], regA.m128);

        // Copy regB in workingArrayImag[k]
        vst1q_f32(&myFFT->workingArrayImag[indexArray], regB.m128);

    }

    // *************************************************************************
    // * STEP 1: Perform computations for all levels except two last one       *
    // *************************************************************************

    // Loop for the groups
    indexGroup = 0;

    for (indexLevel = 0; indexLevel < (myFFT->FFT_NBLEVELS - 2); indexLevel++)
    {

        for (indexArray = 0; indexArray < (myFFT->FFT_SIZE/8); indexArray++)
        {

            // Load arguments aReal, aImag, bReal and bImag
            regA.m128 = vld1q_f32(myFFT->simdARealGroups[indexGroup]);
            regB.m128 = vld1q_f32(myFFT->simdAImagGroups[indexGroup]);
            regC.m128 = vld1q_f32(myFFT->simdBRealGroups[indexGroup]);
            regD.m128 = vld1q_f32(myFFT->simdBImagGroups[indexGroup]);

            // First addition: (aReal + bReal), (aImag + bImag)
            regE.m128 = vaddq_f32(regA.m128,regC.m128);
            regF.m128 = vaddq_f32(regB.m128,regD.m128);

            // Store A = (aReal + bReal) + j(aImag + bImag)
            vst1q_f32(myFFT->simdARealGroups[indexGroup], regE.m128);
            vst1q_f32(myFFT->simdAImagGroups[indexGroup], regF.m128);

            // Second addition: B = (aReal - bReal), (aImag - bImag)
            regE.m128 = vsubq_f32(regA.m128,regC.m128);
            regF.m128 = vsubq_f32(regB.m128,regD.m128);

            // Load twiddle factor WnReal and WnImag
            regA.m128 = vld1q_f32(myFFT->simdRRealGroups[indexGroup]);
            regB.m128 = vld1q_f32(myFFT->simdRImagGroups[indexGroup]);

            // Multiplications

            // (E*A - F*B)
            regC.m128 = vmulq_f32(regE.m128,regA.m128);
            regD.m128 = vmulq_f32(regF.m128,regB.m128);
            regG.m128 = vsubq_f32(regC.m128,regD.m128);

            // (F*A + E*B)
            regC.m128 = vmulq_f32(regF.m128,regA.m128);
            regD.m128 = vmulq_f32(regE.m128,regB.m128);
            regA.m128 = vaddq_f32(regC.m128,regD.m128);

            // Store B = (aReal - bReal) * WnReal - (aImag - bImag) * WnImag
            //         + j[ (aImag - bImag) * WnReal + (aReal - bReal) * WnImag ]

            vst1q_f32(myFFT->simdBRealGroups[indexGroup], regG.m128);
            vst1q_f32(myFFT->simdBImagGroups[indexGroup], regA.m128);

            // Increment the counter
            indexGroup++;

        }

    }

    // *************************************************************************
    // * STEP 2: Perform computations for level 1                              *
    // *************************************************************************

    accumulatorA = 0;

    // Loop for each group in the current level
    for (indexGroup = 0; indexGroup < myFFT->FFT_SIZE/4; indexGroup++)
    {

        // Calculate the indexes
        a = accumulatorA;
        b = accumulatorA + 2;
        accumulatorA++;

        // Load the values a and b (these are complex values)
        valueAReal = myFFT->workingArrayReal[a];
        valueAImag = myFFT->workingArrayImag[a];
        valueBReal = myFFT->workingArrayReal[b];
        valueBImag = myFFT->workingArrayImag[b];

        // Apply A = a + b
        newValueAReal = valueAReal + valueBReal;
        newValueAImag = valueAImag + valueBImag;

        // Apply B = a - b
        newValueBReal = valueAReal - valueBReal;
        newValueBImag = valueAImag - valueBImag;

        // Save results at the same place as the initial values
        myFFT->workingArrayReal[a] = newValueAReal;
        myFFT->workingArrayImag[a] = newValueAImag;
        myFFT->workingArrayReal[b] = newValueBReal;
        myFFT->workingArrayImag[b] = newValueBImag;

        // Calculate the indexes
        a = accumulatorA;
        b = accumulatorA + 2;
        accumulatorA+=3;

        // Load the values a and b (these are complex values)
        valueAReal = myFFT->workingArrayReal[a];
        valueAImag = myFFT->workingArrayImag[a];
        valueBReal = myFFT->workingArrayReal[b];
        valueBImag = myFFT->workingArrayImag[b];

        // Apply A = a + b
        newValueAReal = valueAReal + valueBReal;
        newValueAImag = valueAImag + valueBImag;

        // Apply B = (a - b) * -j = [(aReal - bReal) + j * (aImag - bImag)] * -j =
        //                          (aImag - bImag) + j * (bReal - aReal)
        newValueBReal = valueAImag - valueBImag;
        newValueBImag = valueBReal - valueAReal;

        // Save results at the same place as the initial values
        myFFT->workingArrayReal[a] = newValueAReal;
        myFFT->workingArrayImag[a] = newValueAImag;
        myFFT->workingArrayReal[b] = newValueBReal;
        myFFT->workingArrayImag[b] = newValueBImag;

    }

    // *************************************************************************
    // * STEP 3: Perform computations for level 0                              *
    // *************************************************************************

    accumulatorA = 0;

    // Loop for each group in the current level
    for (indexGroup = 0; indexGroup < myFFT->FFT_SIZE/2; indexGroup++)
    {

        // Calculate the indexes
        a = accumulatorA;
        b = accumulatorA + 1;
        accumulatorA+=2;

        // Load the values a and b (these are complex values)
        valueAReal = myFFT->workingArrayReal[a];
        valueAImag = myFFT->workingArrayImag[a];
        valueBReal = myFFT->workingArrayReal[b];
        valueBImag = myFFT->workingArrayImag[b];

        // Apply A = a + b
        newValueAReal = valueAReal + valueBReal;
        newValueAImag = valueAImag + valueBImag;

        // Apply B = a - b
        newValueBReal = valueAReal - valueBReal;
        newValueBImag = valueAImag - valueBImag;

        // Save results at the same place as the initial values
        myFFT->workingArrayReal[a] = newValueAReal;
        myFFT->workingArrayImag[a] = newValueAImag;
        myFFT->workingArrayReal[b] = newValueBReal;
        myFFT->workingArrayImag[b] = newValueBImag;

    }

    // *************************************************************************
    // * STEP 4: Copy result                                                   *
    // *************************************************************************

    // Reorder result (it is actually in reverse bit order) and copy to destination array
    for (indexRevBitOrder = 0; indexRevBitOrder < myFFT->FFT_SIZE; indexRevBitOrder++)
    {

        destArrayReal[indexRevBitOrder] = myFFT->workingArrayReal[myFFT->revBitOrderArray[indexRevBitOrder]];
        destArrayImag[indexRevBitOrder] = myFFT->workingArrayImag[myFFT->revBitOrderArray[indexRevBitOrder]];

    }
}
