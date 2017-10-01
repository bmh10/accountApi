# Account API

### General Info

* App uses embedded Jetty for web server
* Jersey + JAX-RS libraries are used to provide RESTful API
* Tests are written using JUnit and Mockito

### Starting the server

* To compile and run tests from command line: mvn clean install
* To run the server from command line: mvn exec:java -Dexec.mainClass=org.account.App
* If on Linux run.sh can be executed to perform both of the above
* Once the server is running, the account API can be interacted with - see accountApiExamples.sh for example usage

### Assumptions

* Can only transfer money between accounts that use same currency for simplicity
* Accounts cannot be overdrawn

### Further Improvements

* Maintain a transaction audit log which can be used for analysis or replayed in event of transaction rollback failures
* Use a real database and use built-in rollback capabilities - current rollback logic is flawed
* Add additional endpoints for managing account e.g. update account holder, close account etc.
  This would introduce extra complexity as, for example, an account modify/close call could be made while money is being transferred.
