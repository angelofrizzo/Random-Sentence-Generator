## 1. Overview

The random sentence generator is a Java application that aims to generate random sentences based on a sentence entered by the user.

The application relies on the Google Cloud API to perform the syntactic analysis of the sentence in order to extract tokens with which to fill templates.

The application has an internal record of words and an internal record of tense. The application try to fill a "random" template with user token, if it is not enough uses internal words.

### 1.1 Features

* **Random Sentence Generation:** Generates a randomized sentence based on a user-provided input string.
* **Multiple Sentence Generation:** Allows the user to generate multiple output sentences from the same input string.
* **Tense Selection:** Provides the user with the ability to choose the grammatical tense for the generated sentences.
* **Add and remove words**: Enables the user to add new words to, and later remove words from, an internal vocabulary.
* **Change context:** Allows the user to completely replace the internal vocabulary based on a given text.

---

## 2. Prerequisites

To build and run this application, you will need the following installed on your system:

* **Java Development Kit (JDK):** Version 17 or later.
* **Apache Maven:** Version 3.6.x or later.
* **GCP Authentication:**
    * Authentication credentials configured to allow the application to access Google Cloud services. Setting the `GOOGLE_APPLICATION_CREDENTIALS` environment variable to point to a service account key JSON file.
* **Git:** (Optional, for cloning the project repository).

---

## 3. Setup & Building the Project

1. **Clone the Repository:**
    ```bash
    git clone https://github.com/angelofrizzo/progetto-ingegneria-software
    cd progetto-ingegneria-software
    ```

2. To build the project and package it into an executable JAR file, navigate to the root directory of the project (where the `pom.xml` file is located) and run the following Maven command:
   ```bash
    mvn spring-boot:run
   ```
