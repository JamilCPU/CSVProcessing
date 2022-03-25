# CSVProcessing

Program runs based on command line inputs.

The first set of inputs are
query fileName clients.csv ClientNumber(within clients.csv) someCurrencyabbrev(ex.jpy)
  Example input :
    query jim.json clients.csv 836 aud
    
   When the program receives query it will...
   Create a file, fileName and write to it the specific client found using ClientNumber.
   clients.csv Is the csv file we are pulling data from.
   Putting in some currency will look up data on it and print out the name of the currency and the current rate of exchange.
   
An alternative set of inputs are
report fileName
  Example input :
    report jim.json
    
   When the program receives report it will...
   Read from the specified file and convert it to a markdown file.
   The specified file is expected to be the file produced from running query beforehand.
