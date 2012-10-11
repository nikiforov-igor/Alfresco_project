/**
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */

var Filters =
{
    /**
     * Types that we want to suppress from the resultset
     */
    IGNORED_TYPES:
        [
            "cm:systemfolder",
            "fm:forums",
            "fm:forum",
            "fm:topic",
            "fm:post"
        ],

    /**
     * Create filter parameters based on input parameters
     *
     * @method getFilterParams
     * @param filter {string} Required filter
     * @param parsedArgs {object} Parsed arguments object literal
     * @return {object} Object literal containing parameters to be used in Lucene search
     */
    getFilterParams: function Filter_getFilterParams(filter, parsedArgs)
    {
        var filterParams =
        {
            query: "+PARENT:\"" + parsedArgs.nodeRef + "\" ",
            limitResults: null,
            sort: [
                {
                    column: "@cm:name",
                    ascending: true
                }],
            language: "lucene",
            templates: null
        };
        var columns = filter.filterData.split('#');

        // Max returned results specified?
        var argMax = args.max;
        if ((argMax !== null) && !isNaN(argMax))
        {
            filterParams.limitResults = argMax;
        }
        var params = "";
        for (var i=0; i < columns.length; i++) {
            var or = " OR",
                ampersand = " @";
            var namespace = columns[i].split(":");
            if (columns[i+1] == undefined ) {
                or = "";
                ampersand = " @";
            }

            params += ampersand + namespace[0]+"\\:" + namespace[1] + ":"+ namespace[2]  + or;
        }
        filterParams.query += " AND " + "(" + params + " )";

        return filterParams;
    }
};
