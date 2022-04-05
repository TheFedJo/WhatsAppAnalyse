## WhatsAppAnalyse (Java 11)
##### Een tool om WhatsApp-chatexports weer te geven in statistieken

WhatsAppMessageParser is om de export naar het eigen formaat (class WhatsAppMessage) om te zetten.
WhatsAppMessage heeft 4 velden: tijdstip (LocalDateTime) met getters getLocalDate en getLocalTime,
auteur, berichttype (Enum MessageType), en content, alles met toepasselijke getter. Class Main bevat
alles om de statistieken uit de data te onttrekken en de main method zelf natuurlijk. Bestandslocatie
aanpassen voor je eigen exports natuurlijk. 











 
 
# 
#
### TO_DO
* Javadoc en/of comments
* Informatiedichtheid berekenen (Shannon)
* Berichten per tijdspanne (per auteur)
* Main class splitsen?