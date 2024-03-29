
Opentox-cli is an [OpenTox REST API](http://opentox.org/dev/apis) Java client library

Download form [Maven repository](https://nexus.ideaconsult.net/#nexus-search;quick~opentox-cli) 


### Latest artifact: 

#### Release
```` 
<dependency>
  <groupId>ambit</groupId>
  <artifactId>opentox-client</artifactId>
  <version>3.1.0</version>
</dependency>
````

#### Development
AMBIT 4.1.0-SNAPSHOT dependency
```` 
<dependency>
  <groupId>ambit</groupId>
  <artifactId>opentox-client</artifactId>
  <version>4.1.0-SNAPSHOT</version>
</dependency>
````

### Repositories
````
  <repositories>
    <repository>
      <snapshots>
        <enabled>false</enabled>
      </snapshots>
      <id>nexus-idea-releases</id>
      <name>nexus-idea-releases</name>
      <url>https://nexus.ideaconsult.net/content/repositories/releases</url>
    </repository>
    <repository>
      <releases>
        <enabled>false</enabled>
      </releases>
      <id>nexus-idea-snapshots</id>
      <name>nexus-idea-snapshots</name>
      <url>https://nexus.ideaconsult.net/content/repositories/snapshots</url>
    </repository>
  </repositories>
````

## OpenTox API extensions

### AMBIT JSON extension 
   
[API docs](http://ideaconsult.github.io/apps-ambit/apidocs/)

[More](https://github.com/ideaconsult/apps-ambit/tree/master/ambit-json-docs)

## Command line example client
Note: this is for illustration purposes, does not cover the entire [REST Ambit API](https://github.com/ideaconsult/apps-ambit/tree/master/ambit-json-docs)

````
java -jar mcli.jar -h
AMBIT REST client
 -d,--model_uri <uri>         model_uri
 -e,--feature_uri <uri>       feature_uri
 -h,--help                    AMBIT REST client
 -o,--output <output>         Output file name ( .sdf | .txt  | .csv |
                              .cml | .n3 ) - recognised by extension!
 -p,--page <pagenumber>       Page number, starts with 0, default 0
 -q,--query <query>           search term  e.g. search=50-00-0
 -r,--resource <uri>          resource: feature, compound, dataset, model,
                              algorithm:
 -s,--server <uri>            Root server URI e.g.
                              http://localhost:8080/ambit2
 -t,--querytype <querytype>   auto | similarity | smarts
 -u,--compound_uri <uri>      compound_uri
 -z,--pagesize <pagesize>     Page size, default 10
````

### Examples:

Retrieve compound JSON representation:
```
java -jar mcli.jar -s http://localhost:8080/ambit2 -r compound -compound_uri http://localhost:8080/ambit2/compound/100
```

Run model predictions on compound :
````
java -jar mcli.jar -s http://localhost:8080/ambit2 -r compound --compound_uri http://localhost:8080/ambit2/compound/100 -t predict --model_uri http://localhost:8080/ambit2/model/28
````

Retrieve metadata of first 10 datasets
````
java -jar mcli.jar -s http://localhost:8080/ambit2 -r dataset -z 10 -p 0 -o test.csv
````

Retrieve first 5 features
````
java -jar mcli.jar  -s http://localhost:8080/ambit2 -r feature -z 5
````

Retrieve first 10 models
````
java -jar mcli.jar -s http://localhost:8080/ambit2 -r model -z 10 -p 0 -o test.csv
````

Search similar compounds to c1ccccc1O
````
java -jar mcli.jar -s http://localhost:8080/ambit2 -r querycompound -z 10 -p 0 -q c1ccccc1O -t similarity
````

Search substructure c1ccccc1O
````
java -jar mcli.jar -s http://localhost:8080/ambit2 -r querycompound -z 10 -p 0 -q c1ccccc1O -t smarts
````

Retrieve substances with study summaries 

[Example](https://github.com/ideaconsult/apps-ambit/blob/master/ambit-json-docs/substance_withstudysummary.md)








