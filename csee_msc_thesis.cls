%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%% LaTeX document class: csee_msc_thesis.cls
%% Book document class Master's theses written
%% at the Dept. of Computer Science and Elctrical 
%% Engineering.
%%
%% Created by: Johan Carlson, July 7, 2004.
%%
%% Derived from the Standard LaTeX Book document class.
%%
%% This document class is free to use, copy, distribute,
%% and modify, as long as reference to original author
%% is given.
%%
%% Current version: 1.7
%% Last modified by: Johan Carlson, November 27, 2009.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% *******************************************************************
% Identification
% *******************************************************************
\NeedsTeXFormat{LaTeX2e}
\ProvidesClass{csee_msc_thesis}
\def\theclassversion{1.7}
\def\theclassdate{November 27, 2009}

\newcommand{\lang}{1} % 1=english, 2=swedish
\newcommand{\parseparation}{1} % 1 = indentation at new paragraph
                               % 2 = blank line at new paragraph

% *******************************************************************
% Declare Options
% *******************************************************************
\DeclareOption{sv}{\renewcommand{\lang}{2}}
\DeclareOption{en}{\renewcommand{\lang}{1}}
\DeclareOption{eng}{\renewcommand{\lang}{1}}

\DeclareOption{parindent}{\renewcommand{\parseparation}{1}}
\DeclareOption{parskip}{\renewcommand{\parseparation}{2}}

% Do not know how to handle other options and therefore
% just pass them on to 'book'
\DeclareOption*{\PassOptionsToClass{\CurrentOption}{book}}

% *******************************************************************
% Process Options
% *******************************************************************
\ProcessOptions*

% *******************************************************************
% Load Used Packages and Classes
% *******************************************************************
\LoadClass{book}
\RequirePackage{graphicx}
\RequirePackage{fancyhdr}
\RequirePackage{amsmath}
\RequirePackage{amsfonts}
\RequirePackage{amssymb}
\RequirePackage{ifthen}
\RequirePackage[english]{babel}
\RequirePackage[utf8]{inputenc}
\RequirePackage[acronym]{glossaries}

\makeglossaries

% *******************************************************************
% Define some boldface greek letters
% *******************************************************************
\def\bfalpha{\mbox{\boldmath $\alpha$}}
\def\bfbeta{\mbox{\boldmath $\beta$}}
\def\bfgamma{\mbox{\boldmath $\gamma$}}
\def\bfdelta{\mbox{\boldmath $\delta$}}
\def\bfDelta{\mbox{\boldmath $\Delta$}}
\def\bfepsilon{\mbox{\boldmath $\epsilon$}}
\def\bfeta{\mbox{\boldmath $\eta$}}
\def\bfmu{\mbox{\boldmath $\mu$}}
\def\bftheta{\mbox{\boldmath $\theta$}}

% *******************************************************************
% Start defining and redefining old book style stuff
% *******************************************************************

\renewcommand{\sfdefault}{phv}   % Set Helvetica as headline font
\renewcommand{\baselinestretch}{1.05}
\setlength{\topmargin}{0in}
\setlength{\textwidth}{6.25in}
\setlength{\textheight}{8.75in}
\setlength{\evensidemargin}{-4.5mm}
\setlength{\oddsidemargin}{5.5mm}
\setlength{\intextsep}{24pt}
\setlength{\headheight}{15pt}
\setlength{\belowcaptionskip}{12pt}

\setcounter{tocdepth}{1}
\setcounter{topnumber}{2}
\setcounter{totalnumber}{4}
\setcounter{tocdepth}{2}
\renewcommand{\topfraction}{.95}
\renewcommand{\textfraction}{.05}
\renewcommand{\floatpagefraction}{.75}
\renewcommand{\thechapter}{\arabic{chapter}}
\renewcommand{\thesection}{\arabic{chapter}.\arabic{section}}
\renewcommand{\thesubsection}{\arabic{chapter}.\arabic{section}.\arabic{subsection}}
\renewcommand{\theequation}{\arabic{chapter}.\arabic{equation}}
\renewcommand{\thefigure}{\arabic{chapter}.\arabic{figure}}
\renewcommand{\thetable}{\arabic{chapter}.\arabic{table}}
\renewcommand{\chaptername}{Chapter}
\renewcommand{\jot}{.6\baselineskip}

\pagestyle{fancy}
\fancyhead[LE]{}
\fancyhead[RE]{}
\fancyhead[LO]{}
\fancyhead[RO]{}

% *******************************************************************
% Redefine the \cleardoublepage command so that clear pages use the
% "empty" pagestyle
% *******************************************************************
\def\cleardoublepage{\clearpage\if@twoside \ifodd\c@page\else%
    \hbox{}%
    \thispagestyle{empty}%              % Empty header styles
    \newpage%
    \if@twocolumn\hbox{}\newpage\fi\fi\fi}

% *******************************************************************
% The \startpreamble command.
% Generates all the stuff before the first
% "real" chapter starts, like Abstract,
% Title page, TOC, etc..
% *******************************************************************
\newcommand{\startpreamble}[7]
{
%-------------------------------------------------------
%  Make title page
%-------------------------------------------------------
 \thispagestyle{empty}
 \setcounter{page}{1}
 \pagenumbering{roman}
 \begin{center}
   \vspace{1cm} \mbox{}\hrulefill \mbox{}\\[2.5cm]
   {\Huge \sffamily \textbf{#1\\[4cm]}}
   {\LARGE \textbf{#2}}\\[15mm]
   {\large #3}\\[20mm]
   {\large #6} \vfill
   {\large \mbox{}\hrulefill\mbox{}\\ }
 \end{center}
 %\newpage \mbox{}\newpage
 \cleardoublepage
 %
%-------------------------------------------------------
% Make abstract page(s)
%-------------------------------------------------------
\ifthenelse{\equal{\parseparation}{2}}
  {
    \parskip=\baselineskip
    \parindent=0pt
  }
  {
    \parskip=0pt
    \parindent=10pt
  }
\ifthenelse{\equal{\lang}{2}}
  {
    \selectlanguage{swedish}
  }
  {
    \selectlanguage{english}
  }

 {\ifthenelse{\equal{\lang}{2}}
  {
    \chapter*{\hrulefill\\[-4pt]\hfill \sc \bfseries \sffamily 
                  Sammanfattning\\[-22pt]\hrulefill\\}
    #7
    \cleardoublepage
    \chapter*{\hrulefill\\[-4pt]\hfill \sc \bfseries \sffamily 
                  Abstract\\[-22pt]\hrulefill\\}
    #4
  }
  {
    \chapter*{\hrulefill\\[-4pt]\hfill \sc \bfseries \sffamily 
                  Abstract\\[-22pt]\hrulefill\\}
    #4
    \chapter*{\hrulefill\\[-4pt]\hfill \sc \bfseries \sffamily 
                  Sammanfattning\\[-22pt]\hrulefill\\}
    #7
  }}

 \cleardoublepage

%-------------------------------------------------------
% Include preface
%-------------------------------------------------------
 \thispagestyle{empty}
 \setcounter{secnumdepth}{-2}
 {\ifthenelse{\equal{\lang}{2}}
  {
        \chapter*{\hrulefill\\[-4pt]\hfill \sc \bfseries \sffamily 
                  F{\"o}rord\\[-22pt]\hrulefill\\}
  }
  {
        \chapter*{\hrulefill\\[-4pt]\hfill \sc \bfseries \sffamily 
                  Preface\\[-22pt]\hrulefill\\}
  }}
 \setcounter{secnumdepth}{2}
 \noindent #5

 \cleardoublepage

%-------------------------------------------------------
% Make Table of Contents
%-------------------------------------------------------
 \parskip=1.5pt
 \tableofcontents

  \ifthenelse{\equal{\parseparation}{2}}
    {
      \parskip=\baselineskip
      \parindent=0pt
    }
    {
      \parskip=0pt
      \parindent=10pt
    }
 \cleardoublepage

 \printglossaries

 \cleardoublepage
 \setcounter{page}{1}
 \pagenumbering{arabic}
}

% *******************************************************************
% Make chapter headings look nice
% *******************************************************************
%----------------------------------------------
% Make chapter headings look nice
%----------------------------------------------
\def\@chapter[#1]#2{
	 \refstepcounter{chapter}
          \addcontentsline{toc}{chapter}{{\sc\chaptername}\space%
                \textnormal{\thechapter\space--} \sc #1}%
	 \chaptermark{#1}
    \@makechapterhead{#2}%
    \@afterheading%                % No indendation of first row after heading
}

\def\@makechapterhead#1{%
  {\parindent \z@ \raggedleft
    \reset@font
    \vspace*{2cm}
    \hrule
    \vspace*{8\p@}%
    \par
    \noindent{\sc\sffamily\bfseries\Huge{\@chapapp{}}
            \textnormal{\space\bfseries\sffamily\thechapter}}
    \par\nobreak
    \vspace*{8\p@}%
    \par
    \vspace*{1\p@}%
    \hrule
    \vspace*{8\p@}
    \parbox{12.5cm}
     {
       \begin{flushright}
          \Huge \textnormal{\sffamily #1}\par\nobreak
       \end{flushright}
     }
    \vskip 50\p@
  }
}


% *******************************************************************
% Make figure and table captions \it and \small
% *******************************************************************
\long\def\@makecaption#1#2{
  \vskip\abovecaptionskip
  \sbox\@tempboxa{\small{\it{#1: #2}}}
  \ifdim \wd\@tempboxa >\hsize
    \small{\it{#1: #2\par}}
  \else
    \global \@minipagefalse
    \hb@xt@\hsize{\hfil\box\@tempboxa\hfil}
  \fi
  \vskip\belowcaptionskip}

% *******************************************************************
% \makechapter command
% *******************************************************************
\newcommand{\makechapter}[2]
{
  % First argument is an abbreviated version
  % of the chapter name, used as page header
  % Second argument is the chapter.
  \cleardoublepage
  \fancyhf{}
%  \chapter[\sc {\hspace*{12pt}#2}]{#2}  % Use this if you have more than 9 chapters.
  \chapter[\sc #2]{#2}
  \fancyhead[RE]{\sc #1}
  \fancyhead[LE]{\sc \thepage}
  \fancyhead[RO]{\sc \thepage}
  \fancyhead[LO]{\sc{\nouppercase{\rightmark}}}
  \renewcommand{\thesection}{\arabic{chapter}.\arabic{section}}
  \renewcommand{\theequation}{\arabic{chapter}.\arabic{equation}}
}

% *******************************************************************
% \makeappendix command
% *******************************************************************
\renewcommand{\appendix}{
\setcounter{chapter}{0}
}

\newcommand{\makeappendix}[2]
{
  % First argument is an abbreviated version
  % of the chapter name, used as page header
  % Second argument is the chapter.
  \cleardoublepage
  \fancyhf{}
  \renewcommand{\thechapter}{\Alph{chapter}}
  \renewcommand{\chaptername}{Appendix}
  \chapter[\sc #2]{#2}
  \fancyhead[RE]{\sc #1}
  \fancyhead[LE]{\sc \thepage}
  \fancyhead[RO]{\sc \thepage}
  \fancyhead[LO]{\sc{\nouppercase{\rightmark}}}
  \renewcommand{\thesection}{\Alph{chapter}.\arabic{section}}
  \renewcommand{\theequation}{\Alph{chapter}.\arabic{equation}}
  \renewcommand{\thefigure}{\Alph{chapter}.\arabic{figure}}
  \renewcommand{\thetable}{\Alph{chapter}.\arabic{table}}
}

% *******************************************************************
% Sections, subsections, etc.
% *******************************************************************

\renewcommand\section{\@startsection {section}{1}{\z@}%
                                   {-3.5ex \@plus -1ex \@minus -.2ex}%
                                   {2.3ex \@plus.2ex}%
                                   {\sffamily\Large\bfseries}}

\renewcommand\subsection{\@startsection{subsection}{2}{\z@}%
                                     {-3.25ex\@plus -1ex \@minus -.2ex}%
                                     {1.5ex \@plus .2ex}%
                                     {\sffamily\large\bfseries}}
\renewcommand\subsubsection{\@startsection{subsubsection}{3}{\z@}%
                                     {-3.25ex\@plus -1ex \@minus -.2ex}%
                                     {1.5ex \@plus .2ex}%
                                     {\sffamily\normalsize\bfseries}}

% *******************************************************************
% Table of contents
% *******************************************************************
\renewcommand\tableofcontents{%
    \thispagestyle{empty}
    \if@twocolumn
      \@restonecoltrue\onecolumn
    \else
      \@restonecolfalse
    \fi
    \vskip-2cm
    {\protect\noindent\hrulefill\\[8pt] 
      {\parbox{\textwidth}{%
			\hfill \Huge{\sc \bfseries \sffamily \contentsname}}
		}
	 \hrule}
    \vspace*{12pt}
    \@starttoc{toc}%
    \if@restonecol\twocolumn\fi
}
\renewcommand{\glossarysection}[2][\theglstoctitle]{%
    \thispagestyle{empty}
    \if@twocolumn
      \@restonecoltrue\onecolumn
    \else
      \@restonecolfalse
    \fi
    \vskip-2cm
    \def\theglstoctitle{#2}%
    {\protect\noindent\hrulefill\\[8pt] 
      {\parbox{\textwidth}{%
			\hfill \Huge{\sc \bfseries \sffamily \theglstoctitle}}
		}
	 \hrule}
    \vspace*{12pt}
}
