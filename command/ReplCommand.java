/*
 * Copyright (C) 2021 Grakn Labs
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package grakn.console;

import grakn.client.Grakn;
import grakn.common.collection.Pair;
import org.jline.reader.LineReader;

import java.util.Arrays;
import java.util.List;

import static grakn.common.collection.Collections.pair;

public abstract class ReplCommand {
    public static class Exit extends ReplCommand {
        private static String token = "exit";
        private static String helpCommand = token;
        private static String description = "Exit console";
    }

    public static class Help extends ReplCommand {
        private static String token = "help";
        private static String helpCommand = token;
        private static String description = "Print this help menu";
    }

    public static class Clear extends ReplCommand {
        private static String token = "clear";
        private static String helpCommand = token;
        private static String description = "Clear console screen";
    }

    public static abstract class Database extends ReplCommand {
        private static String token = "database";

        public static class List extends ReplCommand.Database {
            private static String token = "list";
            private static String helpCommand = Database.token + " " + token;
            private static String description = "List the databases on the server";
        }

        public static class Create extends ReplCommand.Database {
            private static String token = "create";
            private static String helpCommand = Database.token + " " + token + " " + "<db>";
            private static String description = "Create a database with name <db> on the server";

            private final String database;
            public Create(String database) {
                this.database = database;
            }
            public String database() { return database; }
        }

        public static class Delete extends ReplCommand.Database {
            private static String token = "delete";
            private static String helpCommand = Database.token + " " + token + " " + "<db>";
            private static String description = "Delete a database with name <db> on the server";

            private final String database;
            public Delete(String database) {
                this.database = database;
            }
            public String database() { return database; }
        }
    }

    public static class Transaction extends ReplCommand {
        private static String token = "transaction";
        private static String helpCommand = token + " <db> schema|data read|write";
        private static String description = "Start a transaction to database <db> with schema or data session, with read or write transaction";

        private final String database;
        private final Grakn.Session.Type sessionType;
        private final Grakn.Transaction.Type transactionType;
        public Transaction(String database, Grakn.Session.Type sessionType, Grakn.Transaction.Type transactionType) {
            this.database = database;
            this.sessionType = sessionType;
            this.transactionType = transactionType;
        }
        public String database() { return database; }
        public Grakn.Session.Type sessionType() { return sessionType; }
        public Grakn.Transaction.Type transactionType() { return transactionType; }
    }

    public Database.Create asDatabaseCreate() { return (Database.Create)this; }
    public Database.Delete asDatabaseDelete() { return (Database.Delete)this; }
    public Transaction asTransaction() { return (Transaction) this; }

    public static String getHelpMenu() {
        List<Pair<String, String>> menu = Arrays.asList(
                pair(Database.List.helpCommand, Database.List.description),
                pair(Database.Create.helpCommand, Database.Create.description),
                pair(Database.Delete.helpCommand, Database.Delete.description),
                pair(Transaction.helpCommand, Transaction.description),
                pair(Help.helpCommand, Help.description),
                pair(Clear.helpCommand, Clear.description),
                pair(Exit.helpCommand, Exit.description)
        );
        return Utils.buildHelpMenu(menu);
    }

    public static ReplCommand getCommand(LineReader reader, Printer printer, String prompt) throws InterruptedException {
        ReplCommand command = null;
        while (command == null) {
            String line = Utils.readNonEmptyLine(reader, prompt);
            String[] tokens = Utils.splitLineByWhitespace(line);
            if (tokens.length == 1 && tokens[0].equals(Exit.token)) {
                command = new Exit();
            } else if (tokens.length == 1 && tokens[0].equals(Help.token)) {
                command = new Help();
            } else if (tokens.length == 1 && tokens[0].equals(Clear.token)) {
                command = new Clear();
            } else if (tokens.length == 2 && tokens[0].equals(Database.token) && tokens[1].equals(Database.List.token)) {
                command = new Database.List();
            } else if (tokens.length == 3 && tokens[0].equals(Database.token) && tokens[1].equals(Database.Create.token)) {
                String database = tokens[2];
                command = new Database.Create(database);
            } else if (tokens.length == 3 && tokens[0].equals(Database.token) && tokens[1].equals(Database.Delete.token)) {
                String database = tokens[2];
                command = new Database.Delete(database);
            } else if (tokens.length == 4 && tokens[0].equals(Transaction.token) &&
                    (tokens[2].equals("schema") || tokens[2].equals("data") && (tokens[3].equals("read") || tokens[3].equals("write")))) {
                String database = tokens[1];
                Grakn.Session.Type sessionType = tokens[2].equals("schema") ? Grakn.Session.Type.SCHEMA : Grakn.Session.Type.DATA;
                Grakn.Transaction.Type transactionType = tokens[3].equals("read") ? Grakn.Transaction.Type.READ : Grakn.Transaction.Type.WRITE;
                command = new Transaction(database, sessionType, transactionType);
            } else {
                printer.error("Unrecognised command, please check help menu");
            }
            reader.getHistory().add(line.trim());
        }
        return command;
    }
}
