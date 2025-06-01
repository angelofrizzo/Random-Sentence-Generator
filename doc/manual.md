## 1. Overview

The random sentence generator is a Java application that aims to generate random sentences based on a sentence entered by the user.

### 1.1 Features

* **Random Sentence Generation:** Generates a randomized sentence based on a user-provided input string.
* **Syntax tree:** Application provides the syntax tree of the input string.
* **Multiple Sentence Generation:** Allows the user to generate multiple output sentences from the same input string.
* **Input Toxicity:** Input strings are accepted only if their toxicity level is lower than 0.7.
* **Output Toxicity Check:** It's possible to have the same toxicity level filter on the output strings.
* **Tense Selection:** Provides the user with the ability to choose the grammatical tense for the generated sentences.
* **Add and Remove words**: Enables the user to add new words to, and later remove words from, an internal vocabulary.
* **Change Context:** Allows the user to completely replace the internal vocabulary based on a given text.

---

## 2. Functionality

The application first parses the string entered by the user via the Google Cloud API. Thanks to the Google Cloud API, it is able to associate a WordType to the various tokens that make up a word.

The system maintains an internal record of templates. A template is chosen randomly: this is first filled with usable words entered by the user then completed with an internal record of words.

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

1. Run the Tests:
    ```bash
    mvn test
    ```
   
2. Generate test report:
    ```bash
    mvn surefire-report:report
    ```

3. Visualize test report on your browser:
    ```bash
    open "./target/site/surefire-report.html"
    ```

