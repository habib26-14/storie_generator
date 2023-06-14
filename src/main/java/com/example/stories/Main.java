package com.example.stories;

public class Main {
    public static String convertDateString(String dateString) {
        // Tableaux de conversion pour les noms des mois et des jours de la semaine
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        String[] daysOfWeek = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};

        // Extraire les parties de la date de la chaîne de texte d'origine
        String[] parts = dateString.split(" ");
        String dayOfWeek = parts[0];
        String month = parts[1];
        String day = parts[2];
        String year = parts[5];

        // Trouver l'index du mois dans le tableau des mois
        int monthIndex = 0;
        for (int i = 0; i < months.length; i++) {
            if (month.equals(months[i])) {
                monthIndex = i + 1; // Ajouter 1 pour correspondre aux mois numérotés de 1 à 12
                break;
            }
        }

        // Formater la date dans le format "yyyy-MM-dd"
        String formattedDate = year + "-" + padZero(monthIndex) + "-" + padZero(Integer.parseInt(day));

        return formattedDate;
    }

    // Fonction utilitaire pour ajouter un zéro devant les nombres inférieurs à 10
    private static String padZero(int number) {
        return (number < 10) ? "0" + number : String.valueOf(number);
    }

    public static void main(String[] args) {
        System.out.println(convertDateString("Tue May 09 00:00:00 CEST 2023"));
    }
}
