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
            name: "Arca",
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
                },
                "pagesAfterAndBefor":{
                    "pagesBeforeEachFind": {
                        type: "number",
                        label: "Pàgines anteriors a cada pàgina trobada",
                        attributes:{
                            min:"0",
                            value:"0"
                        }
                    },
                    "pagesAfterEachFind": {
                        type: "number",
                        label: "Pàgines posteriors a cada pàgina trobada",
                        attributes:{
                            min:"0",
                            value:"0"
                        }
                    }
                }
            }
        },
        HD: {
            name: "Hemeroteca Digital",
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

            "fnDrawCallback": function (e) {
                //console.log("DrawCallback",Date.now());
                //alert("Abans d'amagar. Hi ha overlay?");
                showOverlay();
                //alert("Amagat, hi ha overlay?");
            },
            
            
            "fnPreDrawCallback": function (e) {
                //console.log("PreDrawCallback",Date.now());                
                showOverlay('Dibuixant Taula');
                
                //alert("Dibuixant: Hi ha overlay?");
            }

        };


    var showOverlay = (function () {

        var $overlayNode = $('#progress-overlay');
        var $text = $overlayNode.find('.progress-text');

        return function (text) {            
            var $overlayNode = $('#progress-overlay');
            var $text = $overlayNode.find('.progress-text');
        
            //console.log("Estat de l'overlay abans del canvi:", $('#progress-overlay').css('display'));
            
            
            if (text) {
                
                $text.html(text);

                $overlayNode.css('display', 'block');
               
                console.log("Mostrant Overlay");
                

            } else {
                
                $overlayNode.css('display', 'none');
                
                console.log("Amagant Overlay");
            }
            
            //console.log("Estat de l'overlay després del canvi:", $('#progress-overlay').css('display'));
        };

    })();


    var sendSearchDetailRequest = function(id) {
    var url = '/searchDetail/' + encodeURIComponent(id); // TODO: extreure la ruta per facilitar onfigurar-la

            var timecounter = Date.now();

            console.log("[CRONO]", "[SEARCH_DETAIL]", "enviant petició:", (Date.now()-timecounter)/1000);

        $.ajax(
                    {
                        url: url,
                        type: "GET"
                    }
            ).done(function (data) {
                console.log("[CRONO]", "[SEARCH_DETAIL]", "resposta rebuda:", (Date.now()-timecounter)/1000);
                
                $('#resourcesBySearchDialog').html($(data));
                $('#resourcesBySearchDialog').modal();
                console.log("[CRONO]", "[SEARCH_DETAIL]", "resposta afegida:", (Date.now()-timecounter)/1000);
                
                
                initSearchDetail();
                console.log("[CRONO]", "[SEARCH_DETAIL]", "detall inicialitzat:", (Date.now()-timecounter)/1000);
                initFilter();
                console.log("[CRONO]", "[SEARCH_DETAIL]", "filtres inicialitzats:", (Date.now()-timecounter)/1000);
                initExportButton();
                console.log("[CRONO]", "[SEARCH_DETAIL]", "botó exportació inicialitzat:", (Date.now()-timecounter)/1000);
            });
            
            showOverlay("Consultant detall");
    };

    
    var initSearches = function () {
        $.fn.dataTable.moment('DD/MM/YYYY');
        var $queryTable = $('table#searches');
        
        /* Això no funciona perquè el click es resol després de la ordenació
        $queryTable.find('th.show-overlay').on('click', function() {
            showOverlay("ordenant");
            
        });
        $queryTable.on('order.dt', function() {
            showOverlay();
        });
        */
        
        tables.queryTable = $queryTable.DataTable(defaultOptions);


        addQueryTableListener();
        tables.queryTable.on('draw', addQueryTableListener);
        
    };
    
    var addQueryTableListener = function () {
        var $queryTable = $('table#searches');
        
        $queryTable.find('td a').off();
        $queryTable.find('td a').on('click', function (e) {
            e.preventDefault();
            var id = $(this).attr('data-search-id');            
            sendSearchDetailRequest(id);
        });
    };
    
    var addDetailListener = function() {
        $('#resources td a').off();
        
        $('#resources td a').on('click', function (e) {
            e.preventDefault();
            e.stopPropagation();
            
            var id = $(this).attr('data-resource-id');
            var url = '/resourceDetail/' + encodeURIComponent(id); // TODO: extreure la ruta per facilitar configurar-la

            showOverlay("Actualitzant");

            var timecounter = Date.now();

            console.log("[CRONO]", "[RESOURCE_DETAIL]", "enviant petició", 0);
            
            $.ajax(
                    {
                        url: url,
                        type: "GET"
                    }
            ).done(function (data) {
                console.log("[CRONO]", "[RESOURCE_DETAIL]", "petició rebuda:", (Date.now()-timecounter)/1000);
                $('#resourceDetail').html($(data));
                
                console.log("[CRONO]", "[RESOURCE_DETAIL]", "dades del recurs actualitzades", (Date.now()-timecounter)/1000);
                //console.log("Actualitzades les dades del recurs");
                showOverlay();
            });

        });
    };
    
    var initSearchDetail = function () {
        
        var options = $.extend(true, {}, defaultOptions);
        
        options['columnDefs'] = [
            {
                'targets': 6,
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
        
        /* Això no funciona perquè el click es resol després de la ordenació
        $resourcesDatatable.on('order.dt', function() {
            showOverlay();
        });
        
        $resourcesDatatable.find('th.show-overlay').on('click', function() {            
            showOverlay("ordenant");            
        });
        */

        tables.resourcesTable = $resourcesDatatable.DataTable(options);
        
        tables.resourcesTable.on('select deselect', function (e) {
            // TODO: Update el botó d'exportar
           //console.log("Select a la fila");
           var enableExportButton = getSelectedIndex(tables.resourcesTable).length>0;
           
           //console.log("Nombre de selecteds:", getSelectedIndex(tables.resourcesTable));
           var $exportButton = $('#export-selected');
           
            if (enableExportButton) {
                $exportButton.removeClass('disabled');
            } else {
                $exportButton.addClass('disabled');
            }
            
           //console.log("Enabled?", enableExportButton);
        });
        
        
        addDetailListener();
        
        tables.resourcesTable.on('draw', addDetailListener);
        

    };


    

    var __updateOneSearchGroupForm = function($field, field, extraQueryParams){
            var $label = $('<label>');
            $label.attr('for', field);
            $label.html(extraQueryParams[field].label);
            $field.append($label);

            var $input;

            switch (extraQueryParams[field].type) {
                // TODO[Xavi]: si es necessari afegir altres tipus

                default:
                    $input = $('<input>');
                    $input.attr('type', extraQueryParams[field].type);
            }

            $input.attr('name', field);
            $input.attr('id', field);
            if("placeholder" in extraQueryParams[field]){
                $input.attr('placeholder', extraQueryParams[field].placeholder);
            }
            if("attributes" in extraQueryParams[field]){
                for(var att in extraQueryParams[field].attributes){
                    $input.attr(att, extraQueryParams[field].attributes[att]);
                }
            }
            $input.addClass('form-control');

            $field.append($input);                
    };

    var updateSearchForm = function () {

        var $repository = $('#repository');
        var key = $repository.val();

        var repository = repositoryConfig[key];
        var $extraParams = $('#extra-params');


        // console.log("Updating search form", key, repository.extraQueryParams);

        $extraParams.html("");

        for (var field in repository.extraQueryParams) {
            var $field = $("<div>");
            if ("type" in repository.extraQueryParams[field]){
                $field.addClass('form-group');
                __updateOneSearchGroupForm($field, field, repository.extraQueryParams);
            }else{
                $field.addClass('form-group');
                $field.addClass('row');
                var cssclass0 = "col-sm" ;
                var cssclass = "col-sm-" + Math.trunc(12/Object.keys(repository.extraQueryParams[field]).length);
                var i=0;
                for(var ff in repository.extraQueryParams[field]){
                    var $divCol = $("<div>");
                    if(i===0){
                        $divCol.addClass(cssclass0);
                        __updateOneSearchGroupForm($divCol, ff, repository.extraQueryParams[field]);
                        i++;
                    }else{
                        $divCol.addClass(cssclass);
                        __updateOneSearchGroupForm($divCol, ff, repository.extraQueryParams[field]);
                    }
                    $field.append($divCol);
                }
            }

//            var $label = $('<label>');
//            $label.attr('for', field);
//            $label.html(repository.extraQueryParams[field].label);
//            $field.append($label);
//
//            var $input;
//
//            switch (repository.extraQueryParams[field].type) {
//                // TODO[Xavi]: si es necessari afegir altres tipus
//
//                default:
//                    $input = $('<input>');
//                    $input.attr('type', repository.extraQueryParams[field].type);
//            }
//
//            $input.attr('name', field);
//            $input.attr('id', field);
//            $input.attr('placeholder', repository.extraQueryParams[field].placeholder);
//            $input.addClass('form-control');
//
//            $field.append($input);
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
         
            showOverlay("Cercant");
         
         var timecounter = Date.now();

         console.log("[CRONO]", "[SEARCH]", "Enviant petició:", 0);
         
         $.ajax(
                    {
                        url: "/search",
                        type: "POST",
                        data: $("#search-form").serialize(),
                    }
            ).done(function (data) {
                console.log("[CRONO]", "[SEARCH]", "petició rebuda:", (Date.now()-timecounter)/1000);
                $('#searches-container').replaceWith($(data));
                
                initSearches();
                console.log("[CRONO]", "[SEARCH]", "cercas inicialitzades:", (Date.now()-timecounter)/1000);
                
                initFilter();
                console.log("[CRONO]", "[SEARCH]", "filtres inicialits:", (Date.now()-timecounter)/1000);
            
            
            
                var id = $('#searches').attr('data-selected-id');
                
                
                sendSearchDetailRequest(id);
                                
                console.log("[CRONO]", "[SEARCH]", "actualitzat el llistat de cerques", (Date.now()-timecounter)/1000);
                
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
    };

    var updateFilterDate = function ($operator, $date1, $date2) {
        var selected = $operator.val();
        
        //console.log("Actualitant filtre data");

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

        var $filterQueryDateOriginal = $('#filter-query-date-original');
        var $filterQueryDateOriginal1 = $('#filter-query-date-original-1');
        var $filterQueryDateOriginal2 = $('#filter-query-date-original-2');
        var $filterQueryDateUpdate = $('#filter-query-date-update');
        var $filterQueryDateUpdate1 = $('#filter-query-date-update-1');
        var $filterQueryDateUpdate2 = $('#filter-query-date-update-2');

        $filterQueryDateOriginal.off();
        $filterQueryDateOriginal1.off();
        $filterQueryDateOriginal2.off();
        $filterQueryDateUpdate.off();
        $filterQueryDateUpdate1.off();
        $filterQueryDateUpdate2.off();

        $('#filter-query-execute').off();
        $('#filter-query-remove').off();
        $('#filter-date-order').off();       
        $('#filter-execute').off();
        $('#filter-remove').off();
        $('#filter-select-all').off();
        

        $('#filter-query-execute').on('click', function (e) {
            e.preventDefault();
            tables.queryTable.draw();
        });
        


        $('#filter-query-remove').on('click', function (e) {
            e.preventDefault();
            //console.log("Eliminant filtre");

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

            var COL_SELECT = 6;

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

        });

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
        var $formats = $('#export-formats');

        $exportButton.off();
        $exportButton.on('click', function (e) {
            if ($exportButton.hasClass('disabled')) {
                return;
            }
            
            e.preventDefault();

            var selectedIndex = getSelectedIndex(tables.resourcesTable);
            var count = selectedIndex.length;
            $selectedCounter.text(count);
            $exportDialog.modal();

        });

        $formats.val('');

        $formats.off();
        
        $formats.on('input change', function() {
            if ($formats.val().length>0) {
                $exportExecute.removeClass('disabled');
            } else {
                $exportExecute.addClass('disabled');
            }
        });


        $exportExecute.off();
        
        $exportExecute.on('click', function (e) {
            if ($exportExecute.hasClass('disabled')) {
                return;
            }
            e.preventDefault();
            

            $exportDialog.modal('hide');
            
            
            
            
            var selectedIndex = getSelectedIndex(tables.resourcesTable);
            var count = selectedIndex.length;
            var formats = $formats.val();
            
            showOverlay("Exportant");

                        
            //console.log("Selected index:", selectedIndex, formats);
            var ids=[];
            for (var i=0; i<selectedIndex.length; i++) {
                ids.push($(tables.resourcesTable.row(selectedIndex[i]).nodes()[0]).attr('data-id').replace(new RegExp(',', 'g'), "|"));
            }
            
            //console.log("Iniciat export");
            
            formats = formats.replace(new RegExp(' ', 'g'), ',');
            var process = $("#export-process").val();
            
            $.ajax(
                    {
                        url: "/export",
                        type: "POST",
                        data: {'ids[]': ids,'formats': formats, 'process': process}
                    }
            ).done(function (data, textStatus, jqXHR) {
                //console.log("AJAX done");
                
                showOverlay();
                
                $('#exportMessages').html($(data));

                if (data.indexOf('error')===-1) {
                    
                    tables.resourcesTable.rows({selected:true})
                            .every(function(rowIdx, tableLoop, rowLoop) {
                                var oldProcesses = tables.resourcesTable.cell(rowIdx,2).data();
                                    if (oldProcesses.indexOf(process)===-1) {
                                        if (oldProcesses.length>0) {
                                            oldProcesses +=", ";
                                        }                                        
                                        oldProcesses += process;
                                    }                                
                            tables.resourcesTable.cell(rowIdx,2).data(oldProcesses);
                    })
                    .draw();
            
                    tables.resourcesTable.rows().deselect();
                }
                //console.log("Export finalitzat");
                
                
            }).fail(function(jqXHR, textStatus, errorThrown) {
                // resposta retornada am codi d'error
                showOverlay();
                alert("S'ha produït un error");
            
            });
        });

    };



    var init = function () {
        console.log("Init");
        
        initSearches();
        initSearchDetail();
        
        
        initSearchForm();
        initQueryForm();
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

    var isDateInputSupported = function () {
        var input = document.createElement('input');
        input.setAttribute('type','date');
        var notADateValue = 'not-a-date';
        input.setAttribute('value', notADateValue); 
        return (input.value !== notADateValue);
    }
    
    var dateStringToDDMMYYYY = function(dateString, sep){
        var timeTokens = dateString.split(/[/\-]/);
        //var reverse = isDateInputSupported();
        reverse = timeTokens[0].length === 2;
        
        var ret = "";

        if (reverse) {// aaaa/mm/dd
            ret += timeTokens[2];
            ret += "/";
            ret += timeTokens[1];
            ret += "/";
            ret += timeTokens[0];
        }else{
            ret += timeTokens[0];
            ret += "/";
            ret += timeTokens[1];
            ret += "/";
            ret += timeTokens[2];
        }
        
        //console.log("dateStringToDDMMYYY fa alguna cosa? no retorna res", ret);
    }

    var stringToDate = function (dateString) { // reverse pel format aaaa/mm/dd
        var timeTokens = dateString.split(/[/\-]/);
        var day, month, year;
        
        //var reverse = isDateInputSupported();
        reverse = timeTokens[0].length === 4;

        
        if (reverse) {// aaaa/mm/dd
            day = Number(timeTokens[2]);
            month = Number(timeTokens[1])-1;
            year = Number(timeTokens[0]);
        } else {
            day = Number(timeTokens[0]);    
            month = Number(timeTokens[1])-1;
            year = Number(timeTokens[2]);
        }
        return new Date(Date.UTC(year, month, day));
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

        //console.log(tokenString, text);

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
    var data1 = stringToDate($filterDateOriginal1.val());
    var data2 = stringToDate($filterDateOriginal2.val());
    var dataRow = stringToDate(data[COL_DATE_ORIGINAL]);

    //console.log(dateOrder, data1.getTime(), data2.getTime(), dataRow.getTime());

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

            if (dataRow.getTime() > data1.getTime()) {

                return false;
            }
            break;

        case '>':
            if (dataRow.getTime() < data1.getTime()) {
                return false;
            }

            break;

        case '^':
            if (dataRow.getTime() < data1.getTime() || dataRow.getTime() > data2.getTime()) {
                return false;
            }

            break;

    }


    // Filtre per data update
    dateOrder = $filterDateUpdate.val();
    data1 = stringToDate($filterDateUpdate1.val());
    data2 = stringToDate($filterDateUpdate2.val());
    dataRow = stringToDate(data[COL_DATE_UPDATE]);
    
    // ALERTA: Duplicat més amunt per l'altre formulari, aquest correspon a la consulta de cerques
    //console.log(dateOrder, data1.getTime(), data2.getTime(), dataRow.getTime());
    
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

            if (dataRow.getTime() > data1.getTime()) {

                return false;
            }
            break;

        case '>':
            if (dataRow.getTime() < data1.getTime()) {
                return false;
            }

            break;

        case '^':
            if (dataRow.getTime() < data1.getTime() || dataRow.getTime() > data2.getTime()) {
                return false;
            }

            break;

    }


    // Filtre per criteris


    var querySelected = $filterQuery.val();


    if (querySelected.length > 0) {

        //console.log(querySelected, data[COL_QUERY]);

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
            COL_FORMATS = 5;

    var stringToDate = function (dateString, reverse) { // reverse pel format aaaa/mm/dd <-- ALERTA[Xavi] el peràmetre reverse sempre es undefined?
        
        //console.log("String to date afegida a la taula", dateString, reverse);
        
        var timeTokens = dateString.split(/[/\-]/);
        var day, month, year;
        
        if(timeTokens.length<3 && dateString.search(/[0-9]{0,4}\s+(Gen|Ene|Jan|Feb|Mar|Abr|Apr|Mai|May|Jun|Jul|Ago|Aug|Sep|Set|Oct|Nov|Des|Dic|Dec).*(\s+[1-9]{1,2}st|th|nd|rd)?\s+[0-9]{2,4}/i)>-1){
            var smonth = dateString.match(/Gen|Ene|Jan|Feb|Mar|Abr|Apr|Mai|May|Jun|Jul|Ago|Aug|Sep|Set|Oct|Nov|Des|Dic|Dec/i);
            var syear = dateString.match(/[0-9]{4}/);
            var sday = dateString.match(/(^|\s)([0-9]{1,2}(\s|$))|(^|\s)[0-9]{1,2}(?=(st|th|nd|rd))/);
            
            if(smonth!=null){
                smonth = smonth[0];
            }else{
                smonth="00";
            }
            
            switch (smonth.toLowerCase()){
                case "gen":
                case "ene":
                case "jan":
                    smonth = "01";
                    break;
                case "feb":
                    smonth = "02";
                    break;
                case "mar":
                    smonth = "03";
                    break;
                case "abr":
                case "apr":
                    smonth = "04";
                    break;
                case "mai":
                case "may":
                    smonth = "05";
                    break;
                case "jun":
                    smonth = "06";
                    break;
                case "jul":
                    smonth = "07";
                    break;
                case "ago":
                case "aug":
                    smonth = "08";
                    break;
                case "sep":
                case "set":
                case "Jan":
                    smonth = "09";
                    break;
                case "oct":
                    smonth = "10";
                    break;
                case "nov":
                    smonth = "11";
                    break;
                case "des":
                case "dic":
                case "dec":
                    smonth = "12";
                    break;
            }
            if(sday!=null){
                sday= sday[0];
            }else{
                sday="00";
            }
            if(syear!=null){
                syear= syear[0];
            }else{
                syear="0000";
            }
            timeTokens[0]=sday;
            timeTokens[1]=smonth;
            timeTokens[2]=syear;
            reverse=false;
        }

        reverse = timeTokens[0].length ===4;

        if (reverse) {// aaaa/mm/dd
            day = Number(timeTokens[2]);
            month = Number(timeTokens[1]) - 1;
            year = Number(timeTokens[0]);
        } else {
            day = Number(timeTokens[0]);
            month = Number(timeTokens[1]) - 1;
            year = Number(timeTokens[2]);
        }


        //console.log("Conversió de data:", dateString, year, month, day, new Date(Date.UTC(year, month, day)));
        return new Date(Date.UTC(year, month, day));
    };


    var textContainsAnyToken = function (tokenString, text, separator) { // el separador per defecte es l'espai, admet expresió regular

        //console.log("textContainsAnyToken:", tokenString, text, separator);
        
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
            tokenFound = textContainsAnyToken($filterProcess.val().toLowerCase(), data[COL_PROCESS].toLowerCase(), ',');
            //tokenFound = data[COL_PROCESS] === $filterProcess.val();
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
    var data1 = stringToDate($filterDate1.val());
    var data2 = stringToDate($filterDate2.val());
    var dataRow = stringToDate(data[COL_DATA]);

    //console.log("******* FILTRE ******");
    //console.log("\t", dateOrder, dataRow.getTime(), data1.getTime(), data2.getTime());
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
            if (dataRow.getTime() > data1.getTime()) {

                return false;
            }
            break;

        case '>':
            if (dataRow.getTime() < data1.getTime()) {
                return false;
            }

            break;

        case '^':
            if (dataRow.getTime() < data1.getTime() || dataRow.getTime() > data2.getTime()) {
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
