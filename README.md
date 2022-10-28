## WhatsAppAnalyse (Java 11)
##### Een tool om WhatsApp-chatexports weer te geven in statistieken

parse.WhatsAppMessageParser is om de export naar het eigen formaat (class parse.WhatsAppMessage) om te zetten.
parse.WhatsAppMessage heeft 4 velden: tijdstip (LocalDateTime) met getters getLocalDate en getLocalTime,
auteur, berichttype (Enum parse.MessageType), en content, alles met toepasselijke getter. Class main.Main bevat
alles om de statistieken uit de data te onttrekken en de main method zelf natuurlijk. Bestandslocatie
aanpassen voor je eigen exports natuurlijk. 











 
 
# 
#
### TO_DO
* Javadoc en/of comments
* Berichten per tijdspanne (per auteur)
* Grafieken? Iemand suggesties?