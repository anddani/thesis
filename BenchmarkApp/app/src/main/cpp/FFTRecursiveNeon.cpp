#include "FFTRecursiveNeon.h"
#include <arm_neon.h>
#define LOGTAG "FFTLIB"

cd **LUT;
cd I(0.0, 1.0);

void fftRecursiveNeonInit(int N) {
    int i;
    int n_luts = (int)(log(N)/log(2)) - 2;
    LUT = (cd**)malloc(n_luts * sizeof(cd*));
    for(i = 0; i < n_luts; i++) {
        int n = N / pow(2, i);
        LUT[i] = (cd*)memalign(16, n/2 * sizeof(cd));

        int j;
        for(j = 0; j < n/2; j+=4) {
            cd w[4];
            int k;
            for(k = 0; k < 4; k++) {
                double kth = -2 * (j+k) * M_PI / n;
                w[k] = cd(cos(kth), sin(kth));
            }
            LUT[i][j] = cd(w[0].real(), w[1].real());
            LUT[i][j+1] = cd(w[2].real(), w[3].real());
            LUT[i][j+2] = cd(w[0].imag(), w[1].imag());
            LUT[i][j+3] = cd(w[2].imag(), w[3].imag());
        }
    }

}

void fftRecursiveNeon(cd *in, cd* out, int log2stride, int stride, int N) {
    if(N == 2) {
        out[0]   = in[0] + in[stride];
        out[N/2] = in[0] - in[stride];
    }else if(N == 4){
        fftRecursiveNeon(in, out, log2stride+1, stride << 1, N >> 1);
        fftRecursiveNeon(in+stride, out+N/2, log2stride+1, stride << 1, N >> 1);


        cd temp0 = out[0] + out[2];
        cd temp1 = out[0] - out[2];
        cd temp2 = out[1] - I*out[3];
        cd temp3 = out[1] + I*out[3];
        if(log2stride) {
            out[0] = temp0.real() + temp2.real()*I;
            out[1] = temp1.real() + temp3.real()*I;
            out[2] = temp0.imag() + temp2.imag()*I;
            out[3] = temp1.imag() + temp3.imag()*I;
        }else{
            out[0] = temp0;
            out[2] = temp1;
            out[1] = temp2;
            out[3] = temp3;
        }
    }else if(!log2stride){
        fftRecursiveNeon(in, out, log2stride+1, stride << 1, N >> 1);
        fftRecursiveNeon(in+stride, out+N/2, log2stride+1, stride << 1, N >> 1);

        int k;
        for(k=0;k<N/2;k+=4) {
            float32x4_t Ok_re = vld1q_f32((float *)&out[k+N/2]);
            float32x4_t Ok_im = vld1q_f32((float *)&out[k+N/2+2]);
            float32x4_t w_re = vld1q_f32((float *)&LUT[log2stride][k]);
            float32x4_t w_im = vld1q_f32((float *)&LUT[log2stride][k+2]);
            float32x4_t Ek_re = vld1q_f32((float *)&out[k]);
            float32x4_t Ek_im = vld1q_f32((float *)&out[k+2]);
            float32x4_t wOk_re = vsubq_f32(vmulq_f32(Ok_re,w_re),vmulq_f32(Ok_im,w_im));
            float32x4_t wOk_im = vaddq_f32(vmulq_f32(Ok_re,w_im),vmulq_f32(Ok_im,w_re));
            float32x4_t out0_re = vaddq_f32(Ek_re, wOk_re);
            float32x4_t out0_im = vaddq_f32(Ek_im, wOk_im);
            float32x4_t out1_re = vsubq_f32(Ek_re, wOk_re);
            float32x4_t out1_im = vsubq_f32(Ek_im, wOk_im);
            float32x4_t out_0_low = vcombine_f32(vget_low_f32(out0_re), vget_low_f32(out0_im));
            float32x4_t out_0_high = vcombine_f32(vget_high_f32(out0_re), vget_high_f32(out0_im));
            float32x4_t out_1_low = vcombine_f32(vget_low_f32(out1_re), vget_low_f32(out1_im));
            float32x4_t out_1_high = vcombine_f32(vget_high_f32(out1_re), vget_high_f32(out1_im));
            vst1q_f32((float *)(out+k)      , out_0_low);
            vst1q_f32((float *)(out+k+2)    , out_0_high);
            vst1q_f32((float *)(out+k+N/2)  , out_1_low);
            vst1q_f32((float *)(out+k+N/2+2), out_1_high);
        }
    }else{
        fftRecursiveNeon(in, out, log2stride+1, stride << 1, N >> 1);
        fftRecursiveNeon(in+stride, out+N/2, log2stride+1, stride << 1, N >> 1);

        int k;
        for(k=0;k<N/2;k+=4) {
            float32x4_t Ok_re = vld1q_f32((float *)&out[k+N/2]);
            float32x4_t Ok_im = vld1q_f32((float *)&out[k+N/2+2]);
            float32x4_t w_re = vld1q_f32((float *)&LUT[log2stride][k]);
            float32x4_t w_im = vld1q_f32((float *)&LUT[log2stride][k+2]);
            float32x4_t Ek_re = vld1q_f32((float *)&out[k]);
            float32x4_t Ek_im = vld1q_f32((float *)&out[k+2]);
            float32x4_t wOk_re = vsubq_f32(vmulq_f32(Ok_re,w_re),vmulq_f32(Ok_im,w_im));
            float32x4_t wOk_im = vaddq_f32(vmulq_f32(Ok_re,w_im),vmulq_f32(Ok_im,w_re));
            vst1q_f32((float *)(out+k)      , vaddq_f32(Ek_re, wOk_re));
            vst1q_f32((float *)(out+k+2)    , vaddq_f32(Ek_im, wOk_im));
            vst1q_f32((float *)(out+k+N/2)  , vsubq_f32(Ek_re, wOk_re));
            vst1q_f32((float *)(out+k+N/2+2), vsubq_f32(Ek_im, wOk_im));
        }
    }
}
