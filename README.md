# utilities
This holds all my default projects
This is a combination tutorial for api's that i use as well as helper methods to make things easier to use.
For instance plotting is very easy to use as well as File actions like finding recursively in a directory that contains text or all extensions ect.


commit.sh is to be used from git console
simply inside [] are optional with the value after : is the default
./commit.sh <comments> [<branch:master>]

then type your username and password on the prompt

add it in as a maven dependency
Step 1. Add the JitPack repository to your build file
	<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>
Step 2. Add the dependency

	<dependency>
	    <groupId>com.github.unsupo</groupId>
	    <artifactId>utilities</artifactId>
	    <version>133562433b</version>
	</dependency>

[![](https://jitpack.io/v/unsupo/utilities.svg)](https://jitpack.io/#unsupo/utilities)



Plot class is very useful.
It is an attempt to bring matplotlib.pylab to java and include its easy of use

usage:
new Plot(new double[]{1,2,3}, new double[]{2,3,4}).showPlot();

That's it, it will dispaly the plot as expected.  To add another plot to this plot simply.
new Plot(new double[]{1,2,3}, new double[]{2,3,4}).addSeries("plot name",new double[]{3,4,2},new double[]{1,2,5}).showPlot();

Now both plots will be displayed.
Don't want it to create a pop up, instead want a saved fig?
Simply:

new Plot(new double[]{1,2,3}, new double[]{2,3,4}).addSeries("plot name",new double[]{3,4,2},new double[]{1,2,5}).savePlot("hello.png");

now the plot will be saved to hello.png.

Future work includes more matplotlib functionallity interpolation and plot preferences.
