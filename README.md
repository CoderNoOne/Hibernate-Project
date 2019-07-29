# Hibernate project
---
### 1. About

This is a multi layered application that allows you to manage the system of product orders in the chain store. The stores have an assortment of many categories and branches located in different countries. The prodcuts can be ordered by customers from different countries. We assume that the order of the product is synonymous with its purchase. After ordering/purchasing, the product is covered by a two-year warranty under which various warranty services may be provided. 

An error table is attached to the database schema, which stores information about errors that occurred during table management.
In addition to standard database operations, it's possible to calculate various statistics.


![Alt text](https://i.imgur.com/plqJMGq.jpg "EER DIAGRAM")
***

### Prerequisities

* JDK 12 (switch expressions)
* An actual running database (An open sourced **MySQL DB** was used originally)  - no native queries have been used, so the application is  db implementation independent
***
### Build with

* [Maven](https://maven.apache.org/) - Dependency Management
***
### Main dependencies:
* [hibernate](https://hibernate.org/) - the most popular JPA implementation
* [Junit 5](https://junit.org/junit5/docs/current/user-guide/) - one of the most popular unit-testing frameworks in the Java ecosystem
* [Mockito 3](https://site.mockito.org/) -  JAVA-based library that is used to mock interfaces so that a dummy functionality can be added to a mock interface that can be used in unit testing
* [Hamcrest](http://hamcrest.org/JavaHamcrest/) - a framework for writing matcher objects allowing 'match' rules to be defined declaratively
* [lombok](https://projectlombok.org/) - minimized boilerplate code
* [gson](https://github.com/google/gson/blob/master/UserGuide.md) - 
Java-based library to serialize Java objects to JSON and vice versa
* [apache poi](https://poi.apache.org/) - provides pure Java libraries for reading and writing files in Microsoft Office formats like .xlsx
* [javaMail API](https://mvnrepository.com/artifact/javax.mail/mail/1.4.7) - Java API used to send and receive email via SMTP
***
### How to run it

* configure your database connection (database url etc.) - change appropriate properties in **persistence.xml** file
* change project sdk to 12 (Preview) with switch expressions enabled in your project structure

