if [ "$1a" == "a" ]; then 
	echo "must give a commit message"
	exit;
fi;
git add .
git commit -m "$1"
git push -u origin master
