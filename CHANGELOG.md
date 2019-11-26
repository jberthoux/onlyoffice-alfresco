# Change Log

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
