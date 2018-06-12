var bibliotequesAPI = (function () {

    var tables = {};

    var repositoryConfig = {
        BVPH: {
            name: "BVPH",
            extraQueryParams: {
                // "query": { // Això es el default, no s'ha de posar
                //     type: "string",
                //     label: "Criteris",
                //     placeholder: "Criteris de cerca"
                // },
                "date-start": {
                    type: "date",
                    label: "Data d'inici",
                    placeholder: "Data d'inici"
                },
                "date-end": {
                    type: "date",
                    label: "Data final",
                    placeholder: "Data final"
                }
            }
        },
        Arca: {
            name: "Arca"
        }
    };
    
    var defaultOptions = {

            // dom: "Blfrtip",

            dom: "<'row'<'col-sm-3'l><'col-sm-6 text-center'B><'col-sm-3'f>>" +
                    "<'row'<'col-sm-12'tr>>" +
                    "<'row'<'col-sm-5'i><'col-sm-7'p>>",

            buttons: [],

            // "searching": false,

            "pageLength": 10,

            "language": {
                "sProcessing": "Processant...",
                "sLengthMenu": "Mostrar _MENU_ registres per pàgina",
                "sZeroRecords": "No s'ha trobat cap resultat",
                "sEmptyTable": "Cap dada disponible per aquesta taula",
                "sInfo": "Mostrant registres desde _START_ fins a _END_ d'un total de _TOTAL_ registres",
                "sInfoEmpty": "No hi ha cap registre",
                "sInfoFiltered": "(filtrat d'un total de _MAX_ registres)",
                "sInfoPostFix": "",
                "sSearch": "Cercar:",
                "sUrl": "",
                "sInfoThousands": ",",
                "sLoadingRecords": "Carregant...",
                "oPaginate": {
                    "sFirst": "Primer",
                    "sLast": "Últim",
                    "sNext": "Següent",
                    "sPrevious": "Anterior"
                },
                "oAria": {
                    "sSortAscending": ": Activar per ordenar la columna de manera ascendent",
                    "sSortDescending": ": Activar per ordenar la columna de manera descendent"
                },

            },

            "order": [[0, "asc"]],

            // "deferRender" : true,
            // "scrollY":        "150px",
            // "scroller": true,
            // "scrollCollapse": true,
            // "paging": false

            // deferRender:    true,
            // scrollY:        200,
            // scrollCollapse: true,
            //
            // scroller:       true


        };


    var showOverlay = (function () {

        var $overlayNode = $('#progress-overlay');
        var $text = $overlayNode.find('.progress-text');

        return function (text) {
            if (text) {
                $text.html(text);
                $overlayNode.css('display', 'block');

            } else {
                $overlayNode.css('display', 'none');
            }
        }

    })();


    
    var initSearches = function () {
        $.fn.dataTable.moment('DD/MM/YYYY');
        var $queryTable = $('table#searches');
        tables.queryTable = $queryTable.DataTable(defaultOptions);


            $queryTable.find('td a').on('click', function (e) {
            e.preventDefault();
            console.log("Click detectat");
            var id = $(this).attr('data-search-id');
            var url = '/searchDetail/' + id; // TODO: extreure la ruta per facilitar onfigurar-la

            // AJAX per carregar el dialeg dins d'aquest contenidor
            // En acabar mostrar-lo
            $.ajax(
                    {
                        url: url,
                        type: "GET"
                    }
            ).done(function (data) {
                console.log("AJAX done");
                $('#resourcesBySearchDialog').replaceWith($(data));
                $('#resourcesBySearchDialog').modal();
                
                // TODO: Afegir els handlers als botons del dialeg dialeg
                initSearchDetail(); // de moment només reactiva el datatable
                
            });
            
            console.log("petició enviada");

        });
    };
    
    var initSearchDetail = function () {
        
        var options = $.extend(true, {}, defaultOptions);
        
        options['columnDefs'] = [
            {
                'targets': 5,
                'checkboxes': {
                    'selectRow': true
                },
                'orderable': false

            }];

        options['select'] = {
            style: 'multi'
        };

        options['order'] = [[3, 'desc']];


        var $resourcesDatatable = $('table#resources');

        tables.resourcesTable = $resourcesDatatable.DataTable(options);
        
        
        
        // TODO: Inicialitzar els botons
        $('#resources td a').on('click', function (e) {
            e.preventDefault();
            e.stopPropagation();
            console.log("Click detectat");
            
            var id = $(this).attr('data-resource-id');
            var url = '/resourceDetail/' + id; // TODO: extreure la ruta per facilitar onfigurar-la

            // AJAX per carregar el dialeg dins d'aquest contenidor
            // En acabar mostrar-lo
            $.ajax(
                    {
                        url: url,
                        type: "GET"
                    }
            ).done(function (data) {
                console.log("AJAX done");
                $('#resourceDetail').html($(data));
                
                console.log("Actualitzades les dades del recurs");
            });
            
            console.log("petició enviada");

        });

    }


    


    var updateSearchForm = function () {

        var $repository = $('#repository');
        var key = $repository.val();

        var repository = repositoryConfig[key];
        var $extraParams = $('#extra-params');


        // console.log("Updating search form", key, repository.extraQueryParams);

        $extraParams.html("");

        for (var field in repository.extraQueryParams) {
            var $field = $("<div>");
            $field.addClass('form-group');

            var $label = $('<label>');
            $label.attr('for', field);
            $label.html(repository.extraQueryParams[field].label);
            $field.append($label);

            var $input;

            switch (repository.extraQueryParams[field].type) {
                // TODO[Xavi]: si es necessari afegir altres tipus

                default:
                    $input = $('<input>');
                    $input.attr('type', repository.extraQueryParams[field].type);
            }

            $input.attr('name', field);
            $input.attr('id', field);
            $input.attr('placeholder', repository.extraQueryParams[field].placeholder);
            $input.addClass('form-control');

            $field.append($input);
            $extraParams.append($field);

        }


    };


    var initSearchForm = function () {


        var $repository = $('#repository');

        for (var key in repositoryConfig) {

            var $option = $('<option>');
            $option.html(repositoryConfig[key].name);
            $option.val(key);

            $repository.append($option);
        }


        updateSearchForm();

        $repository.on('change', updateSearchForm);
        
        $('#search-button').on('click', function(e) {
            e.preventDefault();
         
            // TODO: mostrar indicador progress
            showOverlay("Cercant");
         
         $.ajax(
                    {
                        url: "/search",
                        type: "POST",
                        data: $("#search-form").serialize(),
                    }
            ).done(function (data) {
                console.log("AJAX done");
                $('#searches').replaceWith($(data));
                initSearches();
            
                // TODO: initTabla searches i botons. Compte! els botons del fitres estaran lligats a la taula original (comprovar també l'altra taula, pasara el mateix)
                
                console.log("Actualitzat el llistat de cerques");
                
                // TODO: desactivar indicador progress
                showOverlay();
            });
            
            
            
        });
        
    };

    var initQueryForm = function () {
        var $repository = $('#filter-repository');

        for (var key in repositoryConfig) {

            var $option = $('<option>');
            $option.html(repositoryConfig[key].name);
            $option.val(key);

            $repository.append($option);
        }
    }

    var updateFilterDate = function ($operator, $date1, $date2) {
        var selected = $operator.val();

        switch (selected) {
            case '*':
                $date1.attr('disabled', true);
                $date2.attr('disabled', true);
                break;

            case '>':
            case '<':
            case '=':
                $date2.attr('disabled', true);
                $date1.attr('disabled', false);
                break;

            case '^':
                $date2.attr('disabled', false);
                $date1.attr('disabled', false);
                break;
        }


    };


    var initFilter = function () {

        initQueryForm();

        $('#filter-query-execute').on('click', function (e) {
            e.preventDefault();
            tables.queryTable.draw();
        });


        var $filterQueryDateOriginal = $('#filter-query-date-original');
        var $filterQueryDateOriginal1 = $('#filter-query-date-original-1');
        var $filterQueryDateOriginal2 = $('#filter-query-date-original-2');
        var $filterQueryDateUpdate = $('#filter-query-date-update');
        var $filterQueryDateUpdate1 = $('#filter-query-date-update-1');
        var $filterQueryDateUpdate2 = $('#filter-query-date-update-2');

        $('#filter-query-remove').on('click', function (e) {
            e.preventDefault();

            $('#filter-repository').val('*');
            $('#filter-repository-not').prop('checked', false);
            $filterQueryDateOriginal.val('*');
            $filterQueryDateOriginal1.val('');
            $filterQueryDateOriginal2.val('');
            $filterQueryDateUpdate.val('*');
            $filterQueryDateUpdate1.val('');
            $filterQueryDateUpdate2.val('');
            $('#filter-query').val('');

            tables.queryTable.draw();

            updateFilterDate($filterQueryDateOriginal, $filterQueryDateOriginal1, $filterQueryDateOriginal2);
            updateFilterDate($filterQueryDateUpdate, $filterQueryDateUpdate1, $filterQueryDateUpdate2);

        });

        $filterQueryDateOriginal.on('change', function () {
            updateFilterDate($filterQueryDateOriginal, $filterQueryDateOriginal1, $filterQueryDateOriginal2);
        });
        updateFilterDate($filterQueryDateOriginal, $filterQueryDateOriginal1, $filterQueryDateOriginal2);


        $filterQueryDateUpdate.on('change', function () {
            updateFilterDate($filterQueryDateUpdate, $filterQueryDateUpdate1, $filterQueryDateUpdate2);
        });
        updateFilterDate($filterQueryDateUpdate, $filterQueryDateUpdate1, $filterQueryDateUpdate2);


        $('#filter-date-order').on('change', function () {
            updateFilterDate($('#filter-date-order'), $('#filter-date-1'), $('#filter-date-2'));
        });
        updateFilterDate($('#filter-date-order'), $('#filter-date-1'), $('#filter-date-2'));


        $('#filter-execute').on('click', function (e) {
            e.preventDefault();
            tables.resourcesTable.draw();

            tables.resourcesTable.columns().checkboxes.deselectAll();
        });

        $('#filter-remove').on('click', function (e) {
            e.preventDefault();

            $('#filter-process').val('*');
            $('#filter-process-not').prop('checked', false);
            $('#filter-date-order').val('*');
            $('#filter-date-1').val('');
            $('#filter-date-2').val('');
            $('#filter-formats').val('');

            updateFilterDate($('#filter-date-order'), $('#filter-date-1'), $('#filter-date-2'));

            tables.resourcesTable.draw();

        });


        $('#filter-select-all').on('click', function (e) {
            e.preventDefault();

            var COL_SELECT = 5;

            var check = function (rows, checked) {

                rows.every(function () {
                    var data = this.data();

                    var $node = $(data[COL_SELECT]);

                    $node.prop('checked', checked);

                    data[COL_SELECT] = $node.text();

                    this.data(data);
                    this.invalidate();
                });


            };

            var filteredRows = tables.resourcesTable.rows({filter: 'applied'});

            // var unfilteredRows = tables.resourcesTable.rows().data();

            // check(unfilteredRows, false);
            check(filteredRows, true);


            tables.resourcesTable.draw();

        })

    };

    var getSelectedIndex = function (table) {

        var rowsSelected = table.rows({selected: true});

        // console.log(rowsSelected.count(), rowsSelected);

        var rowsSelectedIndex = rowsSelected[0];
        return rowsSelectedIndex;
    };

    var initExportButton = function () {
        var $exportButton = $('#export-selected');
        var $selectedCounter = $('#export-count');
        var $exportDialog = $('#export-modal');
        var $exportExecute = $('#export-execute');

        $exportButton.on('click', function (e) {
            e.preventDefault();

            // TODO: Mostrar dialeg
            var selectedIndex = getSelectedIndex(tables.resourcesTable);
            var count = selectedIndex.length;
            $selectedCounter.text(count);
            $exportDialog.modal();

        });

        $exportExecute.on('click', function (e) {
            e.preventDefault();

            $exportDialog.modal('hide');
            // TODO: enviar petició per començar a exportar els recursos.
            showOverlay("0/" + $selectedCounter.text());

        });


    };



    var init = function () {
        console.log("Init");
        
        initSearches();
        initSearchDetail();
        
        
        initSearchForm();
        initFilter();
        initExportButton();



    };

    return {
        init: init
    }
})();


$(document).ready(function () {

    bibliotequesAPI.init();

});


/* Custom filtering function for queries table*/
$.fn.dataTable.ext.search.push(function (settings, data, dataIndex) {


    // Aquest filtre només s'aplica a la taula resources
    if (settings.sTableId !== "searches") {
        return true;
    }

    var COL_REPOSITORY = 0,
            COL_QUERY = 1,
            COL_DATE_ORIGINAL = 2,
            COL_DATE_UPDATE = 3;

    var stringToDate = function (dateString, reverse) { // reverse pel format aaaa/mm/dd
        var timeTokens = dateString.split(/[/\-]/);
        var day, month, year;

        if (reverse) {// aaaa/mm/dd
            day = Number(timeTokens[2]) - 1;
            month = Number(timeTokens[1]) - 1;
            year = Number(timeTokens[0]) - 1;
        } else {
            day = Number(timeTokens[0]) - 1;
            month = Number(timeTokens[1]) - 1;
            year = Number(timeTokens[2]) - 1;
        }

        return new Date(year, month, day);
    };


    var textContainsAnyToken = function (tokenString, text, separator) { // el separador per defecte es l'espai, admet expresió regular

        tokenString = tokenString.toLowerCase();
        text = text.toLowerCase();

        if (!separator) {
            separator = ' ';
        }

        var tokens = tokenString.split(separator);

        for (var token in tokens) {
            if (text.indexOf(tokens[token]) > -1) {
                return true;
            }
        }

        return false;
    };

    var textContainsAllTokens = function (tokenString, text, separator) { // el separador per defecte es l'espai, admet expresió regular

        tokenString = tokenString.toLowerCase();
        text = text.toLowerCase();

        console.log(tokenString, text);

        if (!separator) {
            separator = ' ';
        }

        var tokens = tokenString.split(separator);

        for (var token in tokens) {
            if (text.indexOf(tokens[token]) == -1) {
                return false;
            }
        }

        return true;
    };


    // Procés d'análisi
    var $filterProcess = $('#filter-repository');
    var filterProcessNot = $('#filter-repository-not').prop('checked'); // checkbox per invertir

    var $filterDateOriginal = $('#filter-query-date-original'); // igual, anterior, posterior
    var $filterDateOriginal1 = $('#filter-query-date-original-1');
    var $filterDateOriginal2 = $('#filter-query-date-original-2');

    var $filterDateUpdate = $('#filter-query-date-update'); // igual, anterior, posterior
    var $filterDateUpdate1 = $('#filter-query-date-update-1');
    var $filterDateUpdate2 = $('#filter-query-date-update-2');

    var $filterQuery = $('#filter-query');


    // Filtre per repository
    var tokenFound;

    if ($filterProcess.val() === '*') {
        tokenFound = true;
    } else {
        tokenFound = data[COL_REPOSITORY].toLowerCase() === $filterProcess.val().toLowerCase();
    }

    if (filterProcessNot) {
        tokenFound = !tokenFound;
    }

    if (!tokenFound) {
        return false;
    }


    // Filtre per data original
    var dateOrder = $filterDateOriginal.val();
    var data1 = stringToDate($filterDateOriginal1.val(), true);
    var data2 = stringToDate($filterDateOriginal2.val(), true);
    var dataRow = stringToDate(data[COL_DATE_ORIGINAL]);

    switch (dateOrder) {

        case '*':
            // No cal comprovar res;
            break;

        case '=':
            if (dataRow.getTime() !== data1.getTime()) {
                return false;
            }

            break;

        case '<':

            if (dataRow > data1) {

                return false;
            }
            break;

        case '>':
            if (dataRow < data1) {
                return false;
            }

            break;

        case '^':
            if (dataRow < data1 || dataRow > data2) {
                return false;
            }

            break;

    }


    // Filtre per data update
    dateOrder = $filterDateUpdate.val();
    data1 = stringToDate($filterDateUpdate1.val(), true);
    data2 = stringToDate($filterDateUpdate2.val(), true);
    dataRow = stringToDate(data[COL_DATE_UPDATE]);

    switch (dateOrder) {

        case '*':
            // No cal comprovar res;
            break;

        case '=':
            if (dataRow.getTime() !== data1.getTime()) {
                return false;
            }

            break;

        case '<':

            if (dataRow > data1) {

                return false;
            }
            break;

        case '>':
            if (dataRow < data1) {
                return false;
            }

            break;

        case '^':
            if (dataRow < data1 || dataRow > data2) {
                return false;
            }

            break;

    }


    // Filtre per criteris


    var querySelected = $filterQuery.val();


    if (querySelected.length > 0) {

        console.log(querySelected, data[COL_QUERY]);

        if (!textContainsAllTokens(querySelected, data[COL_QUERY])) {
            return false;
        }

    }

    // S'ha complert amb tots els filtres, es mostra la fila
    return true;
}
);


/* Custom filtering function for resources table*/
$.fn.dataTable.ext.search.push(function (settings, data, dataIndex) {


    // Aquest filtre només s'aplica a la taula resources
    if (settings.sTableId !== "resources") {
        return true;
    }

    var COL_PROCESS = 2,
            COL_DATA = 3,
            COL_FORMATS = 4;

    var stringToDate = function (dateString, reverse) { // reverse pel format aaaa/mm/dd
        var timeTokens = dateString.split(/[/\-]/);
        var day, month, year;

        if (reverse) {// aaaa/mm/dd
            day = Number(timeTokens[2]) - 1;
            month = Number(timeTokens[1]) - 1;
            year = Number(timeTokens[0]) - 1;
        } else {
            day = Number(timeTokens[0]) - 1;
            month = Number(timeTokens[1]) - 1;
            year = Number(timeTokens[2]) - 1;
        }

        return new Date(year, month, day);
    };


    var textContainsAnyToken = function (tokenString, text, separator) { // el separador per defecte es l'espai, admet expresió regular

        if (!separator) {
            separator = ' ';
        }

        var tokens = tokenString.split(separator);

        for (var token in tokens) {
            if (text.indexOf(tokens[token]) > -1) {
                return true;
            }
        }

        return false;
    };

    // Procés d'análisi
    var $filterProcess = $('#filter-process');
    var filterProcessNot = $('#filter-process-not').prop('checked'); // checkbox per invertir

    var $filterDateOrder = $('#filter-date-order'); // igual, anterior, posterior
    var $filterDate1 = $('#filter-date-1');
    var $filterDate2 = $('#filter-date-2');

    var $filterFormats = $('#filter-formats');


    // Filtre per procès d'anàlisi
    var tokenFound;

    if ($filterProcess.val() === '*') {
        tokenFound = true;
    } else {

        if ($filterProcess.val() === '-') {
            tokenFound = data[COL_PROCESS].length === 0;
        } else {
            tokenFound = data[COL_PROCESS] === $filterProcess.val();
        }
    }

    if (filterProcessNot) {
        tokenFound = !tokenFound;
    }

    if (!tokenFound) {
        return false;
    }


    // Filtre per data
    var dateOrder = $filterDateOrder.val();
    var data1 = stringToDate($filterDate1.val(), true);
    var data2 = stringToDate($filterDate2.val(), true);
    var dataRow = stringToDate(data[COL_DATA]);

    switch (dateOrder) {

        case '*':
            // No cal comprovar res;
            break;

        case '=':
            if (dataRow.getTime() !== data1.getTime()) {
                return false;
            }

            break;

        case '<':

            if (dataRow > data1) {

                return false;
            }
            break;

        case '>':
            if (dataRow < data1) {
                return false;
            }

            break;

        case '^':
            if (dataRow < data1 || dataRow > data2) {
                return false;
            }

            break;

    }

    // Filtre per format

    var formatsSelected = $filterFormats.val();
    if (formatsSelected.length > 0) {

        if (!textContainsAnyToken(formatsSelected.toLowerCase(), data[COL_FORMATS].toLowerCase())) {
            return false;
        }

    }

    // S'ha complert amb tots els filtres, es mostra la fila
    return true;
}
);
