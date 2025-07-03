
ngr-property-linking-frontend
================

## Nomenclature


## Technical documentation


### Before running the app (if applicable)

Ensure that you have the latest versions of the required services and that they are running. This can be done via service manager using the NGR_ALL profile.
```
sm2 --start NGR_ALL
sm2 --stop  NGR_PROPERTY_LINKING_FRONTEND
```
### Run local changes:
* `cd` to the root of the project.
* `sbt run`
* `Note` the service will run on port 1503 by default
`Setup your policies:`
    *  make sure `centralised-authorisation-policy-config` is running `sbt run`
    *  run the shell script `runMainPolicyJsonGenerator.sh` found in the `centralised-authorisation-policy-config` repo
    *  stop `CENTRALISED_AUTHORISATION_POLICY_SERVER` in `service manager`
    *  start `CENTRALISED_AUTHORISATION_POLICY_SERVER` in `service manager`
  
`Using our Stub to populate address's` 
* BH1 -> will return two properties
* LS1 -> will return No results found

`Using VOA_MODERNISED_API_STUB  you must populate the stub`
Note: Using VOA_MODERNISED_API_STUB is automatically used for local and shares the same port as VOA_API_PROXY 
* step one: run the shell script found on VOA_MODERNISED_API_STUB that is called data setup.
* step two: you can check if this worked by running VOA_MODERNISED_API_STUB and hitting this url:
* http://localhost:9540/external-ndr-list-api/properties
* step three: this should return properties from the stub.
* Note: CB2 3PF is one of the postcodes on the VOA_MODERNISED_API_STUB

### Running the test suite
```
sbt clean coverage test coverageReport
```
### Further documentation

shuttering:
* `QA` https://catalogue.tax.service.gov.uk/shuttering-overview/frontend/qa?teamName=Non+Domestic+Rates+Reform+Prog.
* `STAGING` https://catalogue.tax.service.gov.uk/shuttering-overview/frontend/staging?teamName=Non+Domestic+Rates+Reform+Prog.

## Licence
This code is open source software licensed under
the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").