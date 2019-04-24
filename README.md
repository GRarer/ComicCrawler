# Comic Crawler
Comic Crawler is a utility for making offline archives of webcomics. It downloads comic images and labels them numerically so that they can easily be viewed in order (even using generic image-viewing software like Windows Photos).

# Usage
Download crawler.jar and run it from the command line.

Usage: java -jar crawler.jar <first_page_url> [-d <directory_name>] [-p <file_prefix>] [-s <comic_url_substring>] [-a] [-n <number>]

At minimum, you must specify the first page of the comic.
```sh
$ java -jar crawler.jar http://www.goodbyetohalos.com/comic/prologue-1/
```
You can also specify how to save the files.
```sh
$ java -jar crawler.jar xkcd.com/1/ -folder "Desktop/XKCD archive" -prefix "XKCD"
```
If you get the "No valid comic found" error, you may need to tweak the parameters used to identify which of the images on a page are part of the comic content.
```sh
$ java -jar crawler.jar ohumanstar.com/comic/chapter-1-title-page/ -substring "Chapter" -alt
```
# Command-Line Options
-d or -directory or -folder : the directory where images will be saved.
The default is <user_name>/Desktop/comics_output

-p or -prefix : optional prefix for output file names

-s or -substring : the substring used to identify comic images.
The crawler looks for this substring to determine which element in the page is the comic image.
The default is '/comics/', which is used in the comic image URLs on most webcomic sites.

-a or -alt : Identify comic images by looking for the comic substring in the image alt-text.
The default is False (look for substring in the image's URL rather than the Alt-text).

-n or -number: The filenames for each image count up from this number.
The default is 1. You can use a different value if you are downloading new images to be added to an existing archive.

# What's this about substrings?
Comic webpages often contain many images that are not part of the comic's content (logos, headers, etc). Comic Crawler needs to know how to identify which images are part of the comic. By default, it looks for the images whose URLs include the substring "/comics/". For sites that use some other format, use the -substring option to change this filter. For sites that don't use a specific URL substring for their comic images, use the -alt flag to instead filter for images that contain a specific substring in their alt-text.


# Legal
Copyright 2018 Grace Rarer

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

Third party libraries used by this project:

| Library           | Author                     | License            |
| ----------------- | -------------------------- | ------------------ |
| Apache Commons IO | Apache Software Foundation | Apache License 2.0 |
| jsoup             | Jonathan Hedley            | MIT License        |
| JCommander        | Cédric Beust               | Apache License 2.0 | 



The offline archives created by this software are intended for personal and archival use only.
Please do not use this software to violate copyright laws.


