Bot Trust
=========

Solution to [Google's code jam Problem A. "Bot Trust"](https://code.google.com/codejam/contest/975485/dashboard) problem for the [August, 2016 Tampa Bay CoderNight](http://www.meetup.com/CoderNight/events/232198451/) "restart."

Approach
========

I thought about using this as an opportunity to finally get python 3 set-up but I ended up settling for python 2.7 to get done quick. Maybe I'll try converting to python 3 later.

I was curious about speed so I found a neat little timer script and include it here (with attribution). To run with the timer, put both scripts in the same directory. Of course, other options are to delete or comment out the "import timing" line to run without the timer. With the import, the timer runs without any call but the log methods can also be called explicitly to time specific blocks. All other imported modules (two in each script) are part of the standard library so no package downloads are needed.

Since the goal was to determine total time needed for a case, rather than simulating position at any time, the program uses variable time steps, simply calculating the time after each button push. This approach should result in faster run times than if a fixed-time step had been used.

Implementation
==============

Moving the robots only took a few lines of code and robot state is encoded in a single python dict. Input of the cases turned out to be more complicated because of the file structure. I didn't bother using the number of cases or number of moves for each case provided in the input file because I didn't think they would help much but I am left wondering if I missed something. 

The code is mostly self documenting with only a few comment lines to explain the logic.
