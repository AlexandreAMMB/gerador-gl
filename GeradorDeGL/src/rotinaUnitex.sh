#!/bin/bash

#O comando para usar este script é: ./rotinaUnitex.sh <TEXTO> <GL> <Arquivo de Resultado>

texto=`echo $1| cut -d '.' -f1`
grafo=`echo $2| cut -d '.' -f1`
saida=$3

mkdir "${texto}_snt" #Cria o diretório onde a análise é realizada

#--------INICIO PREPROCEESSAMENTO DO TEXTO ESCOLHIDO------------
"/home/${USER}/Unitex-GramLab-3.3/App/UnitexToolLogger" Normalize "${texto}.txt" "-r/home/${USER}/workspace/Unitex-GramLab/Unitex/Portuguese (Brazil)/Norm.txt" "--output_offsets=${texto}_snt/normalize.out.offsets" -qutf8-no-bom
	
"/home/${USER}/Unitex-GramLab-3.3/App/UnitexToolLogger" Grf2Fst2 "/home/${USER}/workspace/Unitex-GramLab/Unitex/Portuguese (Brazil)/Graphs/Preprocessing/Sentence/Sentence.grf" -y "--alphabet=/home/${USER}/workspace/Unitex-GramLab/Unitex/Portuguese (Brazil)/Alphabet.txt" -qutf8-no-bom

"/home/${USER}/Unitex-GramLab-3.3/App/UnitexToolLogger" Flatten "/home/${USER}/workspace/Unitex-GramLab/Unitex/Portuguese (Brazil)/Graphs/Preprocessing/Sentence/Sentence.fst2" --rtn -d5 -qutf8-no-bom

"/home/${USER}/Unitex-GramLab-3.3/App/UnitexToolLogger" Fst2Txt "-t${texto}.snt" "/home/${USER}/workspace/Unitex-GramLab/Unitex/Portuguese (Brazil)/Graphs/Preprocessing/Sentence/Sentence.fst2" "-a/home/${USER}/workspace/Unitex-GramLab/Unitex/Portuguese (Brazil)/Alphabet.txt" -M "--input_offsets=${texto}_snt/normalize.out.offsets" "--output_offsets=${texto}_snt/normalize.out.offsets" -qutf8-no-bom

"/home/${USER}/Unitex-GramLab-3.3/App/UnitexToolLogger" Grf2Fst2 "/home/${USER}/workspace/Unitex-GramLab/Unitex/Portuguese (Brazil)/Graphs/Preprocessing/Replace/Replace.grf" -y "--alphabet=/home/${USER}/workspace/Unitex-GramLab/Unitex/Portuguese (Brazil)/Alphabet.txt" -qutf8-no-bom

"/home/${USER}/Unitex-GramLab-3.3/App/UnitexToolLogger" Fst2Txt "-t${texto}.snt" "/home/${USER}/workspace/Unitex-GramLab/Unitex/Portuguese (Brazil)/Graphs/Preprocessing/Replace/Replace.fst2" "-a/home/${USER}/workspace/Unitex-GramLab/Unitex/Portuguese (Brazil)/Alphabet.txt" -R "--input_offsets=${texto}_snt/normalize.out.offsets" "--output_offsets=${texto}_snt/normalize.out.offsets" -qutf8-no-bom

"/home/${USER}/Unitex-GramLab-3.3/App/UnitexToolLogger" Tokenize "${texto}.snt" "-a/home/${USER}/workspace/Unitex-GramLab/Unitex/Portuguese (Brazil)/Alphabet.txt" "--input_offsets=${texto}_snt/normalize.out.offsets" "--output_offsets=${texto}_snt/tokenize.out.offsets" -qutf8-no-bom

"/home/${USER}/Unitex-GramLab-3.3/App/UnitexToolLogger" Dico "-t${texto}.snt" "-a/home/${USER}/workspace/Unitex-GramLab/Unitex/Portuguese (Brazil)/Alphabet.txt" "/home/${USER}/Unitex-GramLab-3.3/Portuguese (Brazil)/Dela/DELACF_PB.bin" "/home/${USER}/Unitex-GramLab-3.3/Portuguese (Brazil)/Dela/DELAF_PB_2018.bin" "/home/${USER}/Unitex-GramLab-3.3/Portuguese (Brazil)/Dela/Dnum.fst2" -qutf8-no-bom

"/home/${USER}/Unitex-GramLab-3.3/App/UnitexToolLogger" SortTxt "${texto}_snt/dlf" "-l${texto}_snt/dlf.n" "-o/home/${USER}/workspace/Unitex-GramLab/Unitex/Portuguese (Brazil)/Alphabet_sort.txt" -qutf8-no-bom

"/home/${USER}/Unitex-GramLab-3.3/App/UnitexToolLogger" SortTxt "${texto}_snt/dlc" "-l${texto}_snt/dlc.n" "-o/home/${USER}/workspace/Unitex-GramLab/Unitex/Portuguese (Brazil)/Alphabet_sort.txt" -qutf8-no-bom

"/home/${USER}/Unitex-GramLab-3.3/App/UnitexToolLogger" SortTxt "${texto}_snt/err" "-l${texto}_snt/err.n" "-o/home/${USER}/workspace/Unitex-GramLab/Unitex/Portuguese (Brazil)/Alphabet_sort.txt" -qutf8-no-bom

"/home/${USER}/Unitex-GramLab-3.3/App/UnitexToolLogger" SortTxt "${texto}_snt/tags_err" "-l${texto}_snt/tags_err.n" "-o/home/${USER}/workspace/Unitex-GramLab/Unitex/Portuguese (Brazil)/Alphabet_sort.txt" -qutf8-no-bom
#--------FIM PREPROCESSAMENTO---------

#--------INICIO APLICAÇÃO DE GL NO TEXTO--------
"/home/${USER}/Unitex-GramLab-3.3/App/UnitexToolLogger" Grf2Fst2 "${grafo}.grf" -y "--alphabet=/home/${USER}/workspace/Unitex-GramLab/Unitex/Portuguese (Brazil)/Alphabet.txt" -qutf8-no-bom

#"/home/${USER}/Unitex-GramLab-3.3/App/UnitexToolLogger" Locate "-t${texto}.snt" "${grafo}.fst2" "-a/home/${USER}/workspace/Unitex-GramLab/Unitex/Portuguese (Brazil)/Alphabet.txt" -L -I -n200 -b -Y --stack_max=1000 --max_matches_per_subgraph=200 --max_matches_at_token_pos=400 --max_errors=50 -qutf8-no-bom

#"/home/${USER}/Unitex-GramLab-3.3/App/UnitexToolLogger" Concord "${texto}_snt/concord.ind" "-fCourier new" -s12 -l40 -r55 --html "-a/home/${USER}/workspace/Unitex-GramLab/Unitex/Portuguese (Brazil)/Alphabet_sort.txt" --CL -qutf8-no-bom

"/home/${USER}/Unitex-GramLab-3.3/App/UnitexToolLogger" Locate "-t${texto}.snt" "${grafo}.fst2" "-a/home/${USER}/workspace/Unitex-GramLab/Unitex/Portuguese (Brazil)/Alphabet.txt" -L -M --all -b -Y --stack_max=1000 --max_matches_per_subgraph=200 --max_matches_at_token_pos=400 --max_errors=50 -qutf8-no-bom

#"/home/${USER}/Unitex-GramLab-3.3/App/UnitexToolLogger" Concord "${texto}_snt/concord.ind" "-m${texto}.txt" -qutf8-no-bom

"/home/${USER}/Unitex-GramLab-3.3/App/UnitexToolLogger" Concord "${texto}_snt/concord.ind" "-m${texto}_snt/r_textoAnalisado.txt" -t -qutf8-no-bom

#--------FIM APLICAÇÃO DE GL----------

#--------GUARDAR AS CORRESPONDENCIAS ENCONTRADAS NO ARQUIVO DE SAIDA-------------
cat "${texto}_snt/r_textoAnalisado.txt" > "${texto}_snt/../${saida}"

#cat "${texto}_snt/concord.ind" >> "${texto}_snt/../${saida}"