# Change Log

## 4.1.1
## Fixed
- Disallow editing when user has no write permissions [#35](https://github.com/ONLYOFFICE/onlyoffice-alfresco/issues/35)
- disallow editing non OOXML files by removing readonly param

## 4.1.0
## Added
- [Force saving](https://api.onlyoffice.com/editors/save#forcesave) for documents. Can be toggled on/off in settings [#23](https://github.com/ONLYOFFICE/onlyoffice-alfresco/issues/23)
- Preview of the documents on `Document Details` page. Can be toggled on/off in settings [#24](https://github.com/ONLYOFFICE/onlyoffice-alfresco/issues/24)
- Read button is now available for all supported documents that cannot be edited [#26](https://github.com/ONLYOFFICE/onlyoffice-alfresco/pull/26)
- `AMP` generation along with `JAR` files [#32](https://github.com/ONLYOFFICE/onlyoffice-alfresco/issues/32)

## Changed
- `docId` is now randomly generated for each editing session

## Fixed
- File versioning tag was not added on document creation
- Convertation cannot be invoked if user has no write access to file
- Fixed an issue when document was saved under a different user than the one that edits a file

## 4.0.2
## Added
 - Ukrainian translations for `Create ...` menu [#22](https://github.com/ONLYOFFICE/onlyoffice-alfresco/pull/22)

## Fixed
 - Creating a new document was opening an editor in a current tab instead of a new one [#28](https://github.com/ONLYOFFICE/onlyoffice-alfresco/issues/28)
 - `Ignore SSL certificate` setting now is taken into consideration when converting a document [#27](https://github.com/ONLYOFFICE/onlyoffice-alfresco/issues/27)

## 4.0.1
## Fixed
 - fixed an issue when trying to create a new file [#18](https://github.com/ONLYOFFICE/onlyoffice-alfresco/issues/18)
 - fixed an issue with ansi encoding for es, fr, de languages [#18](https://github.com/ONLYOFFICE/onlyoffice-alfresco/issues/18)

## 4.0.0
## Added
 - `Create new..` context menu option in Share
 - Ukrainian translation [#14](https://github.com/ONLYOFFICE/onlyoffice-alfresco/issues/14)

## Changed
 - Document Editing Service address is now splitted in two settings: inner address (address that alfresco will use to access service) and public address (address that user will use access editors)

## Fixed
 - an issue with `'` sign in document title [#15](https://github.com/ONLYOFFICE/onlyoffice-alfresco/issues/15)
 - an issue when document wouldn't save due to automatic logoff [#12](https://github.com/ONLYOFFICE/onlyoffice-alfresco/issues/12)
 - an issue when `Edit on ONLYOFFICE` button was available even if the document was blocked by other editors [#13](https://github.com/ONLYOFFICE/onlyoffice-alfresco/issues/13)

## 3.0.0
## Added
- configuration page in administration console
- saving settings will run a set of test to identify potential problems
- jwt support
- ability to convert `.odt .doc .xls .ods .ppt .odp` to Office Open XML

## Changed
- `Edit in ONLYOFFICE` button now displays only for `.docx .xlsx .pptx .csv .txt` formats
- `.csv .txt` formats will be converted to original format after editing
- document editors will now use user locale
