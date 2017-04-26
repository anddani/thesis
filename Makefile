
all:
	latexmk -pvc -pdf report.tex

clean:
	rm -rf *.acn *.aux *.bbl *.blg *.dvi *.fdb_latexmk *.fls *.glg *.glo *.gls *.ist *.lof *.log *.lot *.run.xml *.synctex.gz *.toc
