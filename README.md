## OCReate - OCR-based PDF/Image-to-Text Converter

![Ocreate](https://i.imgur.com/qjRj6ZS.png)

___
The project was done in the scope of CIS322 Software Engineering Course

### About
- Convert various scanned paper documents and images into searchable and editable format
- Automate the process of capturing alphanumeric information
- Export extracted text in different formats (txt, doc and etc.)
- Supports 50+ languages
- Supports punctuatuion marks, digits and diacritics/accented characters
- Supports Armenian

### Prerequisites
- JDK Version 8.0 or higher

### Setup
- Clone the repository
- Run the project either with IDE (preferrably, IntelliJ IDEA) or from the command line by using the following commands:

```
cd src
javac -cp ".;./jars/*" UI.java
java -cp ".;./jars/*" UI
```
### Usage
- The JAR version of the application can be downlaoded from OCReate Itch website (WIP).

### Running the tests

```
cd src
javac -cp ".;./jars/*" ParserTest.java
java -cp ".;./jars/*" org.junit.runner.JUnitCore ParserTest
```
### Built With
* [JavaFX](https://openjfx.io/) - Open source, next generation client application platform
* [Tesseract Engine](https://github.com/tesseract-ocr/tesseract) - Tesseract Open-Source OCR Engine
* [Tess4J](http://tess4j.sourceforge.net/) - A Java JNA wrapper for Tesseract OCR API

### Authors
OCReate Team: Monika, Irina & Grigor

### Acknowledgements
Special thanks to Prof. Arpi Stepanyan for continous support
