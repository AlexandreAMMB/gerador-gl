#!/bin/bash

#O comando para usar este script é: ./rotinaUnitex.sh <TEXTO> <GL> <nome da GL> <Arquivo de Resultado>

#texto=`echo $1| cut -d '.' -f1`
original=`echo $1| cut -d '.' -f1`
grafo=`echo $2| cut -d '.' -f1`
nomeGrafo=$3
saida=$4

#pasta="${grafo}_snt"

auxiliar="${grafo%/*}/${nomeGrafo}"

mkdir "${auxiliar}"

pasta="${auxiliar}/${nomeGrafo}_snt"

mkdir "${pasta}" #Cria o diretório onde a análise é realizada

texto="${pasta}/../${nomeGrafo}"

#cat "${original}.txt"
cat "${original}.txt" > "${texto}.txt"

#--------INICIO PREPROCEESSAMENTO DO TEXTO ESCOLHIDO------------
"/home/${USER}/Unitex-GramLab-3.3/App/UnitexToolLogger" Normalize "${texto}.txt" "-r/home/${USER}/workspace/Unitex-GramLab/Unitex/Portuguese (Brazil)/Norm.txt" "--output_offsets=${pasta}/normalize.out.offsets" -qutf8-no-bom
	
"/home/${USER}/Unitex-GramLab-3.3/App/UnitexToolLogger" Grf2Fst2 "/home/${USER}/workspace/Unitex-GramLab/Unitex/Portuguese (Brazil)/Graphs/Preprocessing/Sentence/Sentence.grf" -y "--alphabet=/home/${USER}/workspace/Unitex-GramLab/Unitex/Portuguese (Brazil)/Alphabet.txt" -qutf8-no-bom

"/home/${USER}/Unitex-GramLab-3.3/App/UnitexToolLogger" Flatten "/home/${USER}/workspace/Unitex-GramLab/Unitex/Portuguese (Brazil)/Graphs/Preprocessing/Sentence/Sentence.fst2" --rtn -d5 -qutf8-no-bom

"/home/${USER}/Unitex-GramLab-3.3/App/UnitexToolLogger" Fst2Txt "-t${texto}.snt" "/home/${USER}/workspace/Unitex-GramLab/Unitex/Portuguese (Brazil)/Graphs/Preprocessing/Sentence/Sentence.fst2" "-a/home/${USER}/workspace/Unitex-GramLab/Unitex/Portuguese (Brazil)/Alphabet.txt" -M "--input_offsets=${pasta}/normalize.out.offsets" "--output_offsets=${pasta}/normalize.out.offsets" -qutf8-no-bom

"/home/${USER}/Unitex-GramLab-3.3/App/UnitexToolLogger" Grf2Fst2 "/home/${USER}/workspace/Unitex-GramLab/Unitex/Portuguese (Brazil)/Graphs/Preprocessing/Replace/Replace.grf" -y "--alphabet=/home/${USER}/workspace/Unitex-GramLab/Unitex/Portuguese (Brazil)/Alphabet.txt" -qutf8-no-bom

"/home/${USER}/Unitex-GramLab-3.3/App/UnitexToolLogger" Fst2Txt "-t${texto}.snt" "/home/${USER}/workspace/Unitex-GramLab/Unitex/Portuguese (Brazil)/Graphs/Preprocessing/Replace/Replace.fst2" "-a/home/${USER}/workspace/Unitex-GramLab/Unitex/Portuguese (Brazil)/Alphabet.txt" -R "--input_offsets=${pasta}/normalize.out.offsets" "--output_offsets=${pasta}/normalize.out.offsets" -qutf8-no-bom

"/home/${USER}/Unitex-GramLab-3.3/App/UnitexToolLogger" Tokenize "${texto}.snt" "-a/home/${USER}/workspace/Unitex-GramLab/Unitex/Portuguese (Brazil)/Alphabet.txt" "--input_offsets=${pasta}/normalize.out.offsets" "--output_offsets=${pasta}/tokenize.out.offsets" -qutf8-no-bom

"/home/${USER}/Unitex-GramLab-3.3/App/UnitexToolLogger" Dico "-t${texto}.snt" "-a/home/${USER}/workspace/Unitex-GramLab/Unitex/Portuguese (Brazil)/Alphabet.txt" "/home/${USER}/Unitex-GramLab-3.3/Portuguese (Brazil)/Dela/DELACF_PB.bin" "/home/${USER}/Unitex-GramLab-3.3/Portuguese (Brazil)/Dela/DELAF_PB_2018.bin" "/home/${USER}/Unitex-GramLab-3.3/Portuguese (Brazil)/Dela/Dnum.fst2" -qutf8-no-bom

"/home/${USER}/Unitex-GramLab-3.3/App/UnitexToolLogger" SortTxt "${pasta}/dlf" "-l${pasta}/dlf.n" "-o/home/${USER}/workspace/Unitex-GramLab/Unitex/Portuguese (Brazil)/Alphabet_sort.txt" -qutf8-no-bom

"/home/${USER}/Unitex-GramLab-3.3/App/UnitexToolLogger" SortTxt "${pasta}/dlc" "-l${pasta}/dlc.n" "-o/home/${USER}/workspace/Unitex-GramLab/Unitex/Portuguese (Brazil)/Alphabet_sort.txt" -qutf8-no-bom

"/home/${USER}/Unitex-GramLab-3.3/App/UnitexToolLogger" SortTxt "${pasta}/err" "-l${pasta}/err.n" "-o/home/${USER}/workspace/Unitex-GramLab/Unitex/Portuguese (Brazil)/Alphabet_sort.txt" -qutf8-no-bom

"/home/${USER}/Unitex-GramLab-3.3/App/UnitexToolLogger" SortTxt "${pasta}/tags_err" "-l${pasta}/tags_err.n" "-o/home/${USER}/workspace/Unitex-GramLab/Unitex/Portuguese (Brazil)/Alphabet_sort.txt" -qutf8-no-bom
#--------FIM PREPROCESSAMENTO---------

#--------INICIO APLICAÇÃO DE GL NO TEXTO--------
"/home/${USER}/Unitex-GramLab-3.3/App/UnitexToolLogger" Grf2Fst2 "${grafo}.grf" -y "--alphabet=/home/${USER}/workspace/Unitex-GramLab/Unitex/Portuguese (Brazil)/Alphabet.txt" -qutf8-no-bom

#"/home/${USER}/Unitex-GramLab-3.3/App/UnitexToolLogger" Locate "-t${texto}.snt" "${grafo}.fst2" "-a/home/${USER}/workspace/Unitex-GramLab/Unitex/Portuguese (Brazil)/Alphabet.txt" -L -I -n200 -b -Y --stack_max=1000 --max_matches_per_subgraph=200 --max_matches_at_token_pos=400 --max_errors=50 -qutf8-no-bom

"/home/${USER}/Unitex-GramLab-3.3/App/UnitexToolLogger" Locate "-t${texto}.snt" "${grafo}.fst2" "-a/home/${USER}/workspace/Unitex-GramLab/Unitex/Portuguese (Brazil)/Alphabet.txt" -L -I --all -b -Y --stack_max=1000 --max_matches_per_subgraph=200 --max_matches_at_token_pos=400 --max_errors=50 -qutf8-no-bom

"/home/${USER}/Unitex-GramLab-3.3/App/UnitexToolLogger" Concord "${pasta}/concord.ind" "-fCourier new" -s12 -l40 -r55 --html "-a/home/${USER}/workspace/Unitex-GramLab/Unitex/Portuguese (Brazil)/Alphabet_sort.txt" --CL -qutf8-no-bom

"/home/${USER}/Unitex-GramLab-3.3/App/UnitexToolLogger" Concord "${pasta}/concord.ind" "-fCourier new" -s12 -l40 -r55 --text "-a/home/${USER}/workspace/Unitex-GramLab/Unitex/Portuguese (Brazil)/Alphabet_sort.txt" --CL -qutf8-no-bom

"/home/${USER}/Unitex-GramLab-3.3/App/UnitexToolLogger" Locate "-t${texto}.snt" "${grafo}.fst2" "-a/home/${USER}/workspace/Unitex-GramLab/Unitex/Portuguese (Brazil)/Alphabet.txt" -L -M --all -b -Y --stack_max=1000 --max_matches_per_subgraph=200 --max_matches_at_token_pos=400 --max_errors=50 -qutf8-no-bom

"/home/${USER}/Unitex-GramLab-3.3/App/UnitexToolLogger" Concord "${pasta}/concord.ind" "-m${texto}.txt" -qutf8-no-bom

"/home/${USER}/Unitex-GramLab-3.3/App/UnitexToolLogger" Concord "${pasta}/concord.ind" "-m${pasta}/r_textoAnalisado.txt" -t -qutf8-no-bom

#--------FIM APLICAÇÃO DE GL----------

#--------GUARDAR .GRF E .FST2 NA PASTA DE RESULTADO--------
mv "${grafo}.grf" "${pasta}/../" 

mv "${grafo}.fst2" "${pasta}/../"

#--------GUARDAR AS CORRESPONDENCIAS ENCONTRADAS NO ARQUIVO DE SAIDA-------------
#cat "${pasta}/r_textoAnalisado.txt" > "${pasta}/../${saida}"

cat "${pasta}/concord.txt" > "${pasta}/../${saida}.txt"

cat "${pasta}/concord.html" > "${pasta}/../${saida}.html"

#cat "${texto}_snt/concord.ind" >> "${texto}_snt/../${saida}"