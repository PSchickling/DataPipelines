# Transformation-Pipeline

Transformation-Pipeline is a Java library to transform data using pipelines. This library aims to provide a
well-readable way to combine and transform data from different sources.

Characteristics:

* Sequential Execution: The implementation is based on the Iterator-Interface, which has the methods _hasNext()_ and
  _next()_. Therefore, every entry is executed after the other.
* Less memory usage: Through the sequential execution with the Iterator-Interface, the library does not need to hold all
  information for the whole time. The best case would be if the pipeline holds only one state of one entry. When
  _next()_ is called, usually, one single element goes through the pipeline and gets transformed.
* Readability: I hope so, but it depends on the developer. I try to show a good-readable style in the demonstration
  project.

The following example shows the basics.

```java
// Define pipeline
Sink<PersonReportingEntity> sink=
        // Load person from source
        new DataSource<>(personRepository.findAll().iterator())
        // Transform persons to names
        .transform(p->p.getName())
        // only names with 5 or more characters
        .filter(name->name.length< 5)
        // print names out
        .sink(System.out.println(name));

// Execute pipeline
        sink.execute();
```

Basics:

* _DataSource_: Usually the beginning of a pipeline. Initially provides data as an iterator.
* _Transformation_: Changes from one data type to another.
* _Filter_: Filters some entries out. Therefore, it reduces the number of entries.
* _Sink_: Iterates through the pipeline

# Demo-Project

A demo project exists to show how the library can be used in praxis. The demo in `./demo/demo-service-next/` is a
service, which you can run as a spring boot application.

## Pipeline

The pipeline in `demo/demo-service-next/src/main/java/de/schiggo/transformer/app/pipeline/SimplePersonPipeline.java`
reads from the Tables `T_Person` and `T_Address`, transforms, filters, and then writes the data to `D_PersonReporting`.
You can explore the tables via the h2-console at http://localhost:8080/h2-console/ using the JDBC
URL `jdbc:h2:mem:testdb` and the username `sa` without a password.

## Setup

Every time, when http://localhost:8080/api/test is called via GET then the pipeline executes.

## Future work