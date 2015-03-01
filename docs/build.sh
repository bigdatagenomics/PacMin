#/usr/bin/env bash

git_version=$(git rev-parse --short HEAD)

output_dir="."
pdf_output="$output_dir/pacmin_v$git_version.pdf"
html_output="$output_dir/pacmin_v$git_version.html"
date_str=$(date '+%Y-%m-%d')

title="PacMin User Guide"
date="$date_str git:$git_version"
author="http://bdgenomics.org/"
highlight_style="tango"

which pandoc >/dev/null 2>&1
if [ $? -ne "0" ]; then
	echo "WARNING! Pandoc not found on path. Documentation will not be generated!"
	exit 0
fi
which pandoc-citeproc >/dev/null 2>&1
if [ $? -ne "0" ]; then
	echo "WARNING! Pandoc-citeproc not found on path. Documentation will not be generated!"
	exit 0
fi

# Generate a PDF of the docs
pandoc -N --template=template.tex \
--filter pandoc-citeproc \
--highlight-style "$highlight_style" \
--variable mainfont="Georgia" \
--variable sansfont="Arial" \
--variable monofont="Andale Mono" \
--variable fontsize=10pt \
--variable version=$git_version \
--variable listings=true \
--variable title="$title" \
--variable date="$date" \
--variable author="$author" \
--toc \
source/*.md -s -S -o $pdf_output

# Generate HTML of the docs
pandoc source/*.md -H style.css -s -S --toc \
--filter pandoc-citeproc \
--highlight-style "$highlight_style" \
--variable title="$title" \
--variable date="$date" \
--variable author="$author" \
-o $html_output
