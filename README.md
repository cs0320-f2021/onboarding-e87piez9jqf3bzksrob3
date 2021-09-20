# README
To build use:
`mvn package`

To run use:
`./run`

This will give you a barebones REPL, where you can enter text and you will be output at most 5 suggestions sorted alphabetically.

To start the server use:
`./run --gui [--port=<port>]`


# Project: Onboarding

Estimated time: 15 hours including setup (oof)

Design choices:\
I did not create any extra classes for this project. Stars are not represented by 
their own class. Given the simplicity of the data required for the star, I decided to 
use an ArrayList of strings instead, and parse values as necessary.\
For randomisation, I choose the k<sup>th</sup> star in the sorted list, and randomly 
select stars with that distance until the total number of stars (distance less than 
and equal) equals k.

Errors/bugs:\
None that I have seen, but I haven't been able to test the program as extensively as I 
would have liked to, so there may be errors I haven't been able to catch so far.