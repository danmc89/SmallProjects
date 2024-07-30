#!/bin/bash
urls=(`ls *.url | sed 's/ /;_;/g'`)
for u in ${urls[@]}
do
	#replace spaces with underscore
	fle="`echo $u | sed 's/;_;/ /g'`"
	repl="`echo $u | sed 's/;_;/_/g'`"
	echo $fle $repl
	mv "$fle" "$repl"

	#remove front number
	repl2="`echo $repl | sed 's/[\(][0-9]*[\)]//g' | sed 's/^[_]//g'`"
	echo $repl1 $repl2
	mv "$repl" "$repl2"

	#place spaces back
	repl3="`echo $repl2 | sed 's/_/ /g'`"
	echo $repl2 $repl3
	mv "$repl2" "$repl3"
done
