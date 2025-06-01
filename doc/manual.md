## 1. Overview

The random sentence generator is a Java application that aims to generate random sentences based on a sentence entered by the user.

### 1.1 Features

* **Random Sentence Generation:** Generates a randomized sentence based on a user-provided input string.
* **Syntax tree:**
* **Multiple Sentence Generation:** Allows the user to generate multiple output sentences from the same input string.
* **Input Toxicity:**
* **Output Toxicity Check:**
* **Tense Selection:** Provides the user with the ability to choose the grammatical tense for the generated sentences.
* **Add and Remove words**: Enables the user to add new words to, and later remove words from, an internal vocabulary.
* **Change Context:** Allows the user to completely replace the internal vocabulary based on a given text.

---

## 2. Functionality

The application first parses the string entered by the user via the Google Cloud API. Thanks to the Google Cloud API, it is able to associate a WordTyep to the various tokens that make up a word.

The system maintains an internal template record from which one is drawn. This is first filled with usable words entered by the user then completed with an internal record of words. In order to generate random sentences the system randomly fishes words and pseudorandomly creates templates to fill in.

To generate the desired verb tense, the templates are divided by verb tense, in fact when you select a particular verb tense the system will simply return a random template among those with the corresponding tense.

In order to add and delete new words to the internal record, the user has the ability to insert words to the internal record, which will be treated in the same way as other words in the record. The user also has the power to delete words he has inserted.

The user also has the option to completely change the words in the internal record. This is done by inserting a long text that contains many different tokens. In general, a text is considered rich enough if it contains a sufficient number of tokens to fill a template for each grammatical tense.

### 2.1 Templates

This application has 63 implemented templates. However, the collection can be expanded by editing the `templates.json` file located in the `./src/resources/records` directory.

### 2.2 Words

It has already been mentioned that words can be added at run-time via the GUI. However, they can also be added at compile-time by editing the `words.json` file located in the `./src/resources/records` directory, paying close attention to assign each word to its corresponding type.

### 2.3 WordType

The currently accepted tokens are listed below. Proper functioning is not guaranteed if words or templates that use previously supported types are added to the `templates.json` and `words.json` files.

| Word Type           | Examples                 |
|---------------------|--------------------------|
| `PRONOUN_PL`        | `they, we`               |
| `PRONOUN_3SG`       | `he, she, it`            |
| `NOUN_SG`           | `cat, tree, ...`         |
| `NOUN_PL`           | `ideas, birds, ...`      |
| `VERB_PRES_3SG`     | `runs, eats, ...`        |
| `VERB_BASE`         | `find, improve, ...`     |
| `VERB_PAST`         | `jumped, read, ...`      |
| `CONJUNCTION`       | `and, but, ...`          |
| `PREPOSITION`       | `on, under, ...`         |
| `ADJ`               | `happy, bright, ...`     |
| `ADV`               | `quickly, silently, ...` |

---

## 3. Dependencies

This application uses the following main dependencies, as defined in the `pom.xml` file:

### 3.1 Main Dependencies

| Group ID                 | Artifact ID                      | Version                       | Scope   |
| ------------------------ |----------------------------------|-------------------------------| ------- |
| `org.springframework.boot` | `spring-boot-starter-web`        | Managed by Spring Boot Parent | compile |
| `org.springframework.boot` | `spring-boot-starter-data-jpa`   | Managed by Spring Boot Parent | compile |
| `org.springframework.boot` | `spring-boot-starter-thymeleaf`  | Managed by Spring Boot Parent | compile |
| `com.h2database`         | `h2`                             | Managed by Spring Boot Parent | runtime |
| `com.google.code.gson`   | `gson`                           | `2.10.1`                      | compile |
| `com.google.cloud`       | `google-cloud-language`          | Managed by Google Cloud BOM   | compile |

### 3.2 Test Dependencies

| Group ID              | Artifact ID               | Version                         | Scope   |
| --------------------- | ------------------------- | ------------------------------- | ------- |
| `org.mockito`         | `mockito-core`            | Managed by Spring Boot Parent | test    |
| `org.mockito`         | `mockito-junit-jupiter`   | Managed by Spring Boot Parent | test    |
| `org.junit.jupiter`   | `junit-jupiter-api`       | Managed by Spring Boot Parent | test    |

### 3.3 Dependency Management

To ensure version consistency for Google Cloud libraries, the project uses the following Bill of Materials (BOM):

| Group ID           | Artifact ID     | Version   | Type  | Scope  |
| ------------------ | --------------- | --------- | ----- | ------ |
| `com.google.cloud` | `libraries-bom` | `26.59.0` | `pom` | `import`|

**Note:** Versions listed as "Managed by Spring Boot Parent" or "Managed by Google Cloud BOM" indicate that the specific version is defined and maintained by these dependency managers to ensure compatibility. The `compile` scope is the default if not otherwise specified.

---

## 4. Prerequisites

To build and run this application, you will need the following installed on your system:

* **Java Development Kit (JDK):** Version 17 or later.
* **Apache Maven:** Version 3.6.x or later.
* **GCP Authentication:**
    * Authentication credentials configured to allow the application to access Google Cloud services. Setting the `GOOGLE_APPLICATION_CREDENTIALS` environment variable to point to a service account key JSON file.
* **Git:** (Optional, for cloning the project repository).

---

## 5. Setup & Building the Project

1. Clone the Repository:
    ```bash
    git clone https://github.com/angelofrizzo/random-sentence-generator
    cd random-sentence-generator
    ```

2. To build the project and package it into an executable JAR file:
   ```bash
   mvn clean package
   mvn compile
   ```

3. To run the project as a file JAR and visualize the GUI in your browser:
   ```bash
   java -jar target/rsg-1.0.jar
   open http://localhost:8080/sentence
   ```

---

## 6. Test Report Generation Tutorial

### Prerequisites
Before running the test report generation, ensure that:
* You have **Maven** installed (`mvn -version` to check).
* Tests are already implemented and ready to execute.

###  Steps to Generate the Report

1. Run the Tests
Open the terminal and navigate to your project directory, then execute:
    ```bash
    mvn test
    ```

2. Generate the Report
Once the tests are complete, generate the Surefire report using:
    ```bash
    mvn surefire-report:report
    ```

3. Locate the Report
The generated report will be saved in:
    ```
    ./target/site
    ```

4. Open the Report
    ```bash
    open "./target/site/surefire-report.html"
    ```
