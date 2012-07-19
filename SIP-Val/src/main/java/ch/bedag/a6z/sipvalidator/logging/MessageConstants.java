package ch.bedag.a6z.sipvalidator.logging;
/**
 * Interface für den Zugriff auf Resourcen aus dem ResourceBundle.
 * 
 * @author razm Daniel Ludin, Bedag AG @version 2.0
 *
 */
public interface MessageConstants {

    // Initialisierung und Parameter-Ueberprüfung
    String ERROR_PARAMETER_USAGE                        = "error.parameter.usage";
    String ERROR_CONFIGURATION_CANNOTCREATECONFIGDIR    = "error.configuration.cannotcreateconfigdir";
    String ERROR_LOGDIRECTORY_NODIRECTORY               = "error.logdirectory.nodirectory";
    String ERROR_LOGDIRECTORY_NOTWRITABLE               = "error.logdirectory.notwritable";
    String ERROR_SIPFILE_NOTAFILE                       = "error.sipfile.notafile";
    String ERROR_SIPFILE_FILENOTEXISTING                = "error.sipfile.filenotexisting";
    String ERROR_LOGGING_NOFILEAPPENDER                 = "error.logging.nofileappender";
    String ERROR_CANNOTCREATEZIP                        = "error.cannotcreatezip";
    String ERROR_PARAMETER_OPTIONAL_1                   = "error.parameter.optional.1";
    String ERROR_PARAMETER_OPTIONAL_2                   = "error.parameter.optional.2";
    String ERROR_DECOMPRESSING                          = "error.decompressing";
    String ERROR_JHOVEAPP_MISSING                       = "error.jhoveapp.missing";
    String ERROR_JHOVECONF_MISSING                      = "error.jhoveconf.missing";
    
    String ERROR_WRONG_JRE                              = "error.wrong.jdk";
    String ERROR_JRE_VERSION_UNREADABLE                 = "error.jre.version.unreadable";
    
    String MESSAGE_TOTAL_VALID                          = "message.total.valid";
    String MESSAGE_TOTAL_INVALID                        = "message.total.invalid";
    
    String MESSAGE_FOOTER_LOG                           = "message.footer.log";
    String MESSAGE_FOOTER_SIP                           = "message.footer.sip";
    
    String MESSAGE_FOOTER_REPORTJHOVE                   = "message.footer.reportjhove";
    String MESSAGE_FOOTER_REPORTPDFTRON                 = "message.footer.reportpdftron";
    
    
    // Globale Meldungen
    String MESSAGE_SIPVALIDATION                        = "message.sipvalidation";
    String MESSAGE_VALIDATION_INTERRUPTED               = "message.validation.interrupted";
    String MESSAGE_VALIDATION_FINISHED                  = "message.validation.finished";
    String MESSAGE_VALIDATION_TOTAL                     = "message.validation.total";
    String MESSAGE_LOGGING_CANNOTCREATE                 = "message.logging.cannotcreate";
    String MESSAGE_MODULE_VALID                         = "message.module.valid";
    String MESSAGE_MODULE_INVALID                       = "message.module.invalid";
    String MESSAGE_MODULE_INVALID_2ARGS                 = "message.module.invalid.2args";

    String MESSAGE_MODULE_Aa                            = "message.module.aa";
    String MESSAGE_MODULE_Ab                            = "message.module.ab";
    String MESSAGE_MODULE_Ac                            = "message.module.ac";
    String MESSAGE_MODULE_Ad                            = "message.module.ad";
    String MESSAGE_MODULE_Ae                            = "message.module.ae";
    String MESSAGE_MODULE_Af                            = "message.module.af";
    String MESSAGE_MODULE_Ba                            = "message.module.ba";
    String MESSAGE_MODULE_Bb                            = "message.module.bb";
    String MESSAGE_MODULE_Bc                            = "message.module.bc";
    String MESSAGE_MODULE_Bd                            = "message.module.bd";
    String MESSAGE_MODULE_Ca                            = "message.module.ca";
    String MESSAGE_MODULE_Cb                            = "message.module.cb";
    String MESSAGE_MODULE_Cc                            = "message.module.cc";
    String MESSAGE_MODULE_Cd                            = "message.module.cd";
    String MESSAGE_MODULE_Ce                            = "message.module.ce";
    String MESSAGE_MODULE_Cf                            = "message.module.cf";
    
    String MESSAGE_STEPERGEBNIS_Aa                      = "message.stepergebnis.aa";
    String MESSAGE_STEPERGEBNIS_Ab                      = "message.stepergebnis.ab";
    String MESSAGE_STEPERGEBNIS_Ac                      = "message.stepergebnis.ac";
    String MESSAGE_STEPERGEBNIS_Ad                      = "message.stepergebnis.ad";
    String MESSAGE_STEPERGEBNIS_Ae                      = "message.stepergebnis.ae";
    String MESSAGE_STEPERGEBNIS_Af                      = "message.stepergebnis.af";
    String MESSAGE_STEPERGEBNIS_Ba                      = "message.stepergebnis.ba";
    String MESSAGE_STEPERGEBNIS_Bb                      = "message.stepergebnis.bb";
    String MESSAGE_STEPERGEBNIS_Bc                      = "message.stepergebnis.bc";
    String MESSAGE_STEPERGEBNIS_Bd                      = "message.stepergebnis.bd";
    String MESSAGE_STEPERGEBNIS_Ca                      = "message.stepergebnis.ca";
    String MESSAGE_STEPERGEBNIS_Cb                      = "message.stepergebnis.cb";
    String MESSAGE_STEPERGEBNIS_Cc                      = "message.stepergebnis.cc";
    String MESSAGE_STEPERGEBNIS_Cd                      = "message.stepergebnis.cd";

    String MESSAGE_DASHES                               = "message.dashes";
    String MESSAGE_INDENT                               = "message.indent";
    String MESSAGE_SLASH                                = "message.slash";

    String MESSAGE_CONFIGURATION_ERROR_1                = "message.configuration.error.1";
    String MESSAGE_CONFIGURATION_ERROR_2                = "message.configuration.error.2";
    String MESSAGE_CONFIGURATION_ERROR_3                = "message.configuration.error.3";
    String MESSAGE_CONFIGURATION_ERROR_NO_SIGNATURE     = "message.configuration.error.no.signature";
    
    String ERROR_CANNOT_INITIALIZE_DROID                = "error.cannot.initialize.droid";
    
    String ERROR_UNKNOWN                                = "error.unknown";
    
    // Modul A Meldungen
    String ERROR_MODULE_A_INCORRECTFILEENDING           = "error.module.a.incorrectfileending";
    String MESSAGE_MODULE_A_DEFLATED                    = "message.module.a.deflated";
    String MESSAGE_MODULE_A_STORED                      = "message.module.a.stored";
    String ERROR_MODULE_A_NOTREADABLE                   = "message.module.a.notreadable";

    // Modul Ac Meldungen
    String MESSAGE_MODULE_AC_NOTALLOWEDFILE             = "message.module.ac.notallowedfile";
    String MESSAGE_MODULE_AC_MISSINGFILE                = "message.module.ac.missingfile";
    String MESSAGE_MODULE_AC_PATHTOOLONG                = "message.module.ac.pathtoolong";
    String MESSAGE_MODULE_AC_FILENAMETOOLONG            = "message.module.ac.filenametoolong";
    
    // Modul Ad Meldungen
    String ERROR_MODULE_AD_CANNOTCREATEOUTPUTFOLDER     = "error.module.ad.cannotcreateoutputfolder";
    String ERROR_MODULE_AD_WRONGNUMBEROFXSDS            = "error.module.ad.wrongnumberofxsds";
    String ERROR_MODULE_AD_METADATA_ERRORS              = "error.module.ad.metadata.errors";
    
    // Modul Ae Meldungen
    String ERROR_MODULE_AE_NOMETADATAFOUND              = "error.module.ae.nometadatafound";
    String MESSAGE_MODULE_AE_ABLIEFERUNGSTYPFILE        = "message.module.ae.ablieferungstypfile";
    String MESSAGE_MODULE_AE_ABLIEFERUNGSTYPGEVER       = "message.module.ae.ablieferungstypgever";
    String ERROR_MODULE_AE_ABLIEFERUNGSTYPUNDEFINED     = "error.module.ae.ablieferungstypundefined";
    
    // Modul Af Meldungen
    String MESSAGE_MODULE_AF_GEVERSIPWITHOUTPRIMARYDATA = "message.module.af.geversipwithoutprimarydata";
    String ERROR_MODULE_AF_FILESIPWITHOUTPRIMARYDATA    = "error.module.af.filesipwithoutprimarydata";
    
    // Modul Ba Meldungen
    String MESSAGE_MODULE_BA_FILEMISSING                = "message.module.ba.filemissing";
    
    // Modul Bb Meldungen
    String ERROR_MODULE_BB_WRONGCHECKSUM                = "error.module.bb.wrongchecksum";
    String ERROR_MODULE_BB_MISSINGINSIP                 = "error.module.bb.missinginsip";
    String ERROR_MODULE_BB_CANNOTPROCESSMD5             = "error.module.bb.cannotprocessmd5";
    String ERROR_MODULE_BB_CANNOTCLOSESTREAMMD5         = "error.module.bb.cannotclosestreammd5";
    
    String MESSAGE_MODULE_BC_FILEMISSING                = "message.module.bc.filemissing";
    
    String MESSAGE_MODULE_AC_INVALIDCHARACTERS          = "message.module.ac.invalidcharacters";
    String MESSAGE_MODULE_AC_INVALIDFILENAME            = "message.module.ac.invalidfilename";
    
    String MESSAGE_MODULE_CA_INVALIDPUID1               = "message.module.ca.invalidpuid1";
    String MESSAGE_MODULE_CA_INVALIDPUID2               = "message.module.ca.invalidpuid2";
    String MESSAGE_MODULE_CA_INVALIDPUID                = "message.module.ca.invalidpuid";
    String MESSAGE_MODULE_CA_INVALIDEXT                 = "message.module.ca.invalidext";
    String MESSAGE_MODULE_CA_UNIDENTIFIED               = "message.module.ca.unidentified";
    String MESSAGE_MODULE_CA_FILES                      = "message.module.ca.files";

    // Modul Bd Meldungen
    String MESSAGE_MODULE_BD_MISSINGINABLIEFERUNG       = "message.module.bd.missinginablieferung";
    String MESSAGE_MODULE_BD_MISSINGINCONTENT           = "message.module.bd.missingincontent";

    // Modul 3c Meldungen
    String MESSAGE_MODULE_CC_CANNOTWRITEJHOVEREPORT     = "message.module.cc.cannotwritejhovereport";
    String MESSAGE_MODULE_CC_INVALID                    = "message.module.cc.invalid";
    String MESSAGE_MODULE_CC_ERRORS_IN                  = "message.module.cc.errors.in";
    String MESSAGE_MODULE_CC_JHOVE_REPORT_MISSING       = "message.module.cc.jhove.report.missing";
    
    // Modul 3d Meldungen
    String ERROR_MODULE_CD_DATUM_VON_AFTER_DATUM_BIS    = "error.module.cd.datum.von.after.datum.bis";
    String ERROR_MODULE_CD_REGISTRIERDATUM_IN_FUTURE    = "error.module.cd.registrierdatum.in.future";
    String ERROR_MODULE_CD_DATUM_ENTSTEHUNG_VON_IN_FUTURE    = "error.module.cd.datum.entstehung.von.in.future";
    String ERROR_MODULE_CD_DATUM_ENTSTEHUNG_BIS_IN_FUTURE    = "error.module.cd.datum.entstehung.bis.in.future";
    String ERROR_MODULE_CD_DATUM_VON_IN_FUTURE          = "error.module.cd.datum.von.in.future";
    String ERROR_MODULE_CD_DATUM_BIS_IN_FUTURE          = "error.module.cd.datum.bis.in.future";
    String ERROR_MODULE_CD_DATUM_VON_NOT_EXISTING       = "error.module.cd.datum.von.not.existing";
    String ERROR_MODULE_CD_DATUM_BIS_NOT_EXISTING       = "error.module.cd.datum.bis.not.existing";
    
    String ERROR_MODULE_CD_INVALID_ABLIEFERUNG_RANGE    = "error.module.cd.invalid.ablieferung.range";
    String ERROR_MODULE_CD_INVALID_DOSSIER_RANGE        = "error.module.cd.invalid.dossier.range";
    String ERROR_MODULE_CD_INVALID_DOSSIER_RANGE_CA     = "error.module.cd.invalid.dossier.range.ca";
    String ERROR_MODULE_CD_INVALID_DOSSIER_RANGE_CA_ABL = "error.module.cd.invalid.dossier.range.ca.abl";
    String ERROR_MODULE_CD_INVALID_DOKUMENT_RANGE       = "error.module.cd.invalid.dokument.range";
    String ERROR_MODULE_CD_INVALID_DOKUMENT_RANGE_CA    = "error.module.cd.invalid.dokument.range.ca";
    String MESSAGE_MODULE_CD_NUMBER_OF_CONTENT_FILES    = "message.module.cd.numberofcontentfiles";
    String ERROR_MODULE_CD_UNPARSEABLE_DATE             = "error.module.cd.unparseable.date";
}
