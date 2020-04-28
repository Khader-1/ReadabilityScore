package readability;

import java.io.File;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try {
            Scanner input = new Scanner(new File(args[0]));
            String text = input.nextLine();
            while (input.hasNext()) {
                text += " " + input.nextLine();
            }
            new Text(text).prompt();
        } catch (Exception e) {
            System.out.println("excption");
        }
    }

    static class Text {

        private String plainText;
        private int numSentences;
        private int numWords;
        private int numChars;
        private int syllables;
        private int polySyllables;

        public Text(String plainText) {
            this.plainText = plainText;
            count();
        }

        private void count() {
            countSentences();
        }

        private void countSentences() {
            String text = plainText.trim();
            String[] sentences = text.split("[\\?!\\.]");
            numSentences = sentences.length;
            countWords(sentences);
        }

        private void countWords(String[] sentences) {
            for (String sen : sentences) {
                sen = sen.trim();
                String[] words = sen.split("\\s");
                numWords += words.length;
                for (String word : words) {
                    word = word.trim();
                    numChars += word.length();
                }
                countSyllabels(words);
            }
            numChars += plainText.endsWith("!") || plainText.endsWith("?") || plainText.endsWith(".") ? numSentences : numSentences - 1;
        }

        private void countSyllabels(String[] words) {
            for (String word : words) {
                int val = 0;
                word = word.trim();
                if (word.matches("\\d")) continue;
                if (word.endsWith("e"))
                    word = word.substring(0, word.length() - 1);
                String syllables[] = word.split("[^AEIOUYaeiouy]+");
                for (String syllable : syllables) {
                    val += syllable.equals("") ? 0 : 1;
                }
                if (val == 0) {
                    val++;
                }
                this.syllables += val;
                if (val > 2) this.polySyllables++;
            }
        }

        private void prompt() {
            System.out.println("The text is: \n" + this.plainText);
            System.out.println();
            System.out.println("Words: " + numWords);
            System.out.println("Sentences: " + numSentences);
            System.out.println("Characters: " + numChars);
            System.out.println("Syllables: " + syllables);
            System.out.println("Polysyllables: " + polySyllables);
            System.out.print("Enter the score you want to calculate (ARI, FK, SMOG, CL, all): ");
            String choice = new Scanner(System.in).next();
            System.out.println();
            double ari = ARI();
            double smog = SMOG();
            double cl = CL();
            double fk = FK();
            int maxAri = max(ari);
            int maxSmog = max(smog);
            int maxCl = max(cl);
            int maxFk = max(fk);
            switch (choice) {
                case "ARI":
                    System.out.println("Automated Readability Index: " + ari + " (about " + printMax(maxAri) + " year olds).");
                    break;
                case "FK":
                    System.out.println("Flesch–Kincaid readability tests: " + fk + " (about " + printMax(maxFk) + " year olds).");
                    break;
                case "SMOG":
                    System.out.println("Simple Measure of Gobbledygook: " + smog + " (about " + printMax(maxSmog) + " year olds).");
                    break;
                case "CL":
                    System.out.println("Coleman–Liau index: " + cl + " (about " + printMax(maxCl) + " year olds).");
                    break;
                default:
                    System.out.println("Automated Readability Index: " + ari + " (about " + printMax(maxAri) + " year olds).");
                    System.out.println("Flesch–Kincaid readability tests: " + fk + " (about " + printMax(maxFk) + " year olds).");
                    System.out.println("Simple Measure of Gobbledygook: " + smog + " (about " + printMax(maxSmog) + " year olds).");
                    System.out.println("Coleman–Liau index: " + cl + " (about " + printMax(maxCl) + " year olds).");
            }
            double avg = (maxAri + maxCl + maxFk + maxSmog) / 4.0;
            System.out.println("This text should be understood in average by " + printMax(avg) + " year olds.");
        }

        private double ARI() {
            double score = (4.71 * numChars / numWords + 0.5 * numWords / numSentences - 21.43);
            return round(score);
        }

        private double FK() {
            double score = 0.39 * numWords / numSentences + 11.8 * syllables / numWords - 15.59;
            return round(score);
        }

        private double SMOG() {
            final double poly = polySyllables;
            final double num = numSentences;
            double score = 1.043 * Math.sqrt(poly * 30 / num) + 3.1291;
            return round(score);
        }

        private double CL() {
            double avgChar = (double) numChars / (double) numWords * 100.0;
            double avgSen = (double) numSentences / (double) numWords * 100.0;
            double score = 0.0588 * avgChar - 0.296 * avgSen - 15.8;
            return round(score);
        }

        private static double round(double score) {
            return ((int) (score * 100) / 100.0);
        }

        private static int max(double val) {
            int rounded = (int) (val - 0.5 >= (int) val ? val + 1 : val);
            switch (rounded) {
                case 1:
                    return 6;
                case 2:
                    return 7;
                case 3:
                    return 9;
                case 13:
                    return 24;
                case 14:
                    return 25;
                default:
                    return (rounded + 6);
            }
        }

        private String printMax(double max) {
            return max > 24 ? "24+" : max + "";
        }
    }
}
