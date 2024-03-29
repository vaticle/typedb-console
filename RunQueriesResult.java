/*
 * Copyright (C) 2022 Vaticle
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

package com.vaticle.typedb.console;

public class RunQueriesResult {
    private final boolean success;
    private final boolean hasChanges;

    public RunQueriesResult(boolean success, boolean hasChanges) {
        this.success = success;
        this.hasChanges = hasChanges;
    }

    public static RunQueriesResult error() {
        return new RunQueriesResult(false, false);
    }

    public boolean success() {
        return success;
    }

    public boolean hasChanges() {
        return hasChanges;
    }
}
