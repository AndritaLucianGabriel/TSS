package service;

import operations.Transaction;
import service.files.ReaderFiles;

import java.time.LocalDate;

public interface AccountStatement {

    default void balanceCheck(double value, String currency) {
        Timestamp.timestamp("AccountStatement,balanceCheck");
        System.out.println("Contul are " + FormatDouble.format(value) + " " + currency);
    }

    default String myTypeOfDateSinceThereIsNoWayToDoItProperly(String date) {
        Timestamp.timestamp("AccountStatement,myTypeOfDateSinceThereIsNoWayToDoItProperly");
        String[] local = date.split("-");
        return local[2] + "-" + local[1] + "-" + local[0];
    }

    default StringBuilder showTransaction(Transaction transaction) {
        StringBuilder text;
        text = new StringBuilder();
        text.append(transaction.getTransactionID()).append(",");
        text.append(transaction.getTimestamp().toString()).append(",");
        text.append(transaction.getValue()).append(",");
        text.append(transaction.getCurrency()).append("\n");
        return text;
    }

    default String filterDate(String IBAN, String startDate, String sign) {
        Timestamp.timestamp("AccountStatement,filterDate");
        StringBuilder text = new StringBuilder();
        LocalDate parsedDate = LocalDate.parse(myTypeOfDateSinceThereIsNoWayToDoItProperly(startDate));
        switch (sign) {
            case ("="): {
                for (Transaction x : ReaderFiles.getInstance().readerAccountStatement(IBAN)) {
                    if (x.getTimestamp().equals(parsedDate)) {
                        text.append(showTransaction(x));
                    }
                }
                break;
            }
            case (">"): {
                for (Transaction x : ReaderFiles.getInstance().readerAccountStatement(IBAN)) {
                    if (x.getTimestamp().compareTo(parsedDate) > 0) {
                        text.append(showTransaction(x));
                    }
                }
                break;
            }
            case ("<"): {
                for (Transaction x : ReaderFiles.getInstance().readerAccountStatement(IBAN)) {
                    if (x.getTimestamp().compareTo(parsedDate) < 0) {
                        text.append(showTransaction(x));
                    }
                }
                break;
            }
            case ("<="): {
                for (Transaction x : ReaderFiles.getInstance().readerAccountStatement(IBAN)) {
                    if (x.getTimestamp().compareTo(parsedDate) <= 0) {
                        text.append(showTransaction(x));
                    }
                }
                break;
            }
            case (">="): {
                for (Transaction x : ReaderFiles.getInstance().readerAccountStatement(IBAN)) {
                    if (x.getTimestamp().compareTo(parsedDate) >= 0) {
                        text.append(showTransaction(x));
                    }
                }
                break;
            }
            case ("<>"): {
                for (Transaction x : ReaderFiles.getInstance().readerAccountStatement(IBAN)) {
                    if (!x.getTimestamp().equals(parsedDate)) {
                        text.append(showTransaction(x));
                    }
                }
            }
            break;
        }
        return text.toString();
    }

    default String filterDate(String IBAN, String startDate, String sign, String stopDate) {
        Timestamp.timestamp("AccountStatement,filterDate");
        StringBuilder text = new StringBuilder();
        LocalDate parsedStartDate = LocalDate.parse(myTypeOfDateSinceThereIsNoWayToDoItProperly(startDate));
        LocalDate parsedStopDate = LocalDate.parse(myTypeOfDateSinceThereIsNoWayToDoItProperly(stopDate));
        switch (sign) {
            case ("><"): {
                for (Transaction x : ReaderFiles.getInstance().readerAccountStatement(IBAN)) {
                    if (x.getTimestamp().compareTo(parsedStartDate) > 0 && x.getTimestamp().compareTo(parsedStopDate) < 0) {
                        text.append(showTransaction(x));
                    }
                }
                break;
            }
            case ("><="): {
                for (Transaction x : ReaderFiles.getInstance().readerAccountStatement(IBAN)) {
                    if (x.getTimestamp().compareTo(parsedStartDate) > 0 && x.getTimestamp().compareTo(parsedStopDate) <= 0) {
                        text.append(showTransaction(x));
                    }
                }
                break;
            }
            case (">=<"): {
                for (Transaction x : ReaderFiles.getInstance().readerAccountStatement(IBAN)) {
                    if (x.getTimestamp().compareTo(parsedStartDate) >= 0 && x.getTimestamp().compareTo(parsedStopDate) < 0) {
                        text.append(showTransaction(x));
                    }
                }
                break;
            }

            case (">=<="): {
                for (Transaction x : ReaderFiles.getInstance().readerAccountStatement(IBAN)) {
                    if (x.getTimestamp().compareTo(parsedStartDate) >= 0 && x.getTimestamp().compareTo(parsedStopDate) <= 0) {
                        text.append(showTransaction(x));
                    }
                }
                break;
            }

            case ("<>"): {
                for (Transaction x : ReaderFiles.getInstance().readerAccountStatement(IBAN)) {
                    if (x.getTimestamp().compareTo(parsedStartDate) < 0 && x.getTimestamp().compareTo(parsedStopDate) > 0) {
                        text.append(showTransaction(x));
                    }
                }
                break;
            }
        }
        return text.toString();
    }

    default String filterValue(String IBAN, double value, String sign) {
        Timestamp.timestamp("AccountStatement,filterValue");
        StringBuilder text = new StringBuilder();
        switch (sign) {
            case ("="): {
                for (Transaction x : ReaderFiles.getInstance().readerAccountStatement(IBAN)) {
                    if (Math.abs(x.getValue()) == value) {
                        text.append(showTransaction(x));
                    }
                }
                break;
            }

            case (">"): {
                for (Transaction x : ReaderFiles.getInstance().readerAccountStatement(IBAN)) {
                    if (Math.abs(x.getValue()) > value) {
                        text.append(showTransaction(x));
                    }
                }
                break;
            }

            case (">="): {
                for (Transaction x : ReaderFiles.getInstance().readerAccountStatement(IBAN)) {
                    if (Math.abs(x.getValue()) >= value) {
                        text.append(showTransaction(x));
                    }
                }
                break;
            }

            case ("<"): {
                for (Transaction x : ReaderFiles.getInstance().readerAccountStatement(IBAN)) {
                    if (Math.abs(x.getValue()) < value) {
                        text.append(showTransaction(x));
                    }
                }
                break;
            }

            case ("<="): {
                for (Transaction x : ReaderFiles.getInstance().readerAccountStatement(IBAN)) {
                    if (Math.abs(x.getValue()) <= value) {
                        text.append(showTransaction(x));
                    }
                }
                break;
            }

            case ("<>"): {
                for (Transaction x : ReaderFiles.getInstance().readerAccountStatement(IBAN)) {
                    if (Math.abs(x.getValue()) != value) {
                        text.append(showTransaction(x));
                    }
                }
                break;
            }

        }
        return text.toString();
    }

    default String filterValue(String IBAN, double minValue, String sign, double maxValue) {
        Timestamp.timestamp("AccountStatement,filterValue");
        StringBuilder text = new StringBuilder();
        switch (sign) {
            case ("><"): {
                for (Transaction x : ReaderFiles.getInstance().readerAccountStatement(IBAN)) {
                    if (Math.abs(x.getValue()) > minValue && Math.abs(x.getValue()) < maxValue) {
                        text.append(showTransaction(x));
                    }
                }
                break;
            }

            case ("><="): {
                for (Transaction x : ReaderFiles.getInstance().readerAccountStatement(IBAN)) {
                    if (Math.abs(x.getValue()) > minValue && Math.abs(x.getValue()) <= maxValue) {
                        text.append(showTransaction(x));
                    }
                }
                break;
            }

            case (">=<"): {
                for (Transaction x : ReaderFiles.getInstance().readerAccountStatement(IBAN)) {
                    if (Math.abs(x.getValue()) >= minValue && Math.abs(x.getValue()) < maxValue) {
                        text.append(showTransaction(x));
                    }
                }
                break;
            }

            case (">=<="): {
                for (Transaction x : ReaderFiles.getInstance().readerAccountStatement(IBAN)) {
                    if (Math.abs(x.getValue()) >= minValue && Math.abs(x.getValue()) <= maxValue) {
                        text.append(showTransaction(x));
                    }
                }
                break;
            }

            case ("<>"): {
                for (Transaction x : ReaderFiles.getInstance().readerAccountStatement(IBAN)) {
                    if (Math.abs(x.getValue()) < minValue && Math.abs(x.getValue()) > maxValue) {
                        text.append(showTransaction(x));
                    }
                }
                break;
            }

        }
        return text.toString();
    }

    default String filterCurrency(String IBAN, String currency, String sign) {
        Timestamp.timestamp("AccountStatement,filterDate");
        StringBuilder text = new StringBuilder();
        switch (sign) {
            case ("="): {
                for (Transaction x : ReaderFiles.getInstance().readerAccountStatement(IBAN)) {
                    if (x.getCurrency().equals(currency)) {
                        text.append(showTransaction(x));
                    }
                }
                break;
            }
            case ("<>"): {
                for (Transaction x : ReaderFiles.getInstance().readerAccountStatement(IBAN)) {
                    if (!x.getCurrency().equals(currency)) {
                        text.append(showTransaction(x));
                    }
                }
                break;
            }
        }
        return text.toString();
    }

}
