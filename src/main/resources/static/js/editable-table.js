var EditableTable = function () {

    return {

        //main function to initiate the module
        init: function () {
            function restoreRow(oTable, nRow) {
                var aData = oTable.fnGetData(nRow);
                var jqTds = $('>td', nRow);
                for (var i = 0, iLen = jqTds.length; i < iLen; i++) {
                    oTable.fnUpdate(aData[i], nRow, i, false);
                }
                oTable.fnDraw();
            }

            function editRow(oTable, nRow, type) {
                var aData = oTable.fnGetData(nRow);
                var jqTds = $('>td', nRow);
                if (type == "用户管理") {
                    jqTds[1].innerHTML = '<input type="text" class="form-control small" value="' + aData[1] + '">';
                    jqTds[2].innerHTML = '<input type="text" class="form-control small" value="' + aData[2] + '">';
                    jqTds[3].innerHTML = '<a class="edit" href="">保存</a>';
                } else if (type == "器材管理") {
                    jqTds[1].innerHTML = '<input type="text" class="form-control small" value="' + aData[1] + '">';
                    jqTds[2].innerHTML = '<input type="text" class="form-control small" value="' + aData[2] + '">';
                    jqTds[4].innerHTML = '<a class="edit" href="">保存</a>';
                }
            }

            function saveRow(oTable, nRow, type) {
                var jqInputs = $('input', nRow);

                if (type == "用户管理") {
                    var name = jqInputs[0].value;
                    var phone = jqInputs[1].value;
                    if (name == "" || name == undefined || phone == "" || phone == undefined) {
                        alert("用户名或手机号不可为空！");
                        return false;
                    }
                    $.ajax({
                        url: "/addUser",
                        data: {"name": name, "phone": phone},
                        type: "post",
                        success: function (data) {
                            if (data.status == "true") {
                                alert(data.reason);
                                oTable.fnUpdate(jqInputs[0].value, nRow, 1, false);
                                oTable.fnUpdate(jqInputs[1].value, nRow, 2, false);
                                oTable.fnUpdate('<a class="delete" href="">删除</a>', nRow, 3, false);
                                oTable.fnDraw();
                                nEditing = null;
                            } else if (data.status == "false") {
                                alert(data.reason);
                            }
                        }
                    });
                } else if (type == "器材管理") {
                    var itemName = jqInputs[0].value;
                    var area = jqInputs[1].value;
                    if (itemName == "" || itemName == undefined || area == "" || area == undefined) {
                        alert("器材名称或厂区不可为空！");
                        return false;
                    }
                    $.ajax({
                        url: "/addItem",
                        data: {"itemName": itemName, "area": area},
                        type: "post",
                        success: function (data) {
                            if (data.status == "true") {
                                alert(data.reason);
                                oTable.fnUpdate(jqInputs[0].value, nRow, 1, false);
                                oTable.fnUpdate(jqInputs[1].value, nRow, 2, false);
                                oTable.fnUpdate('', nRow, 3, false);
                                oTable.fnUpdate('<a class="delete" href="">删除</a>', nRow, 4, false);
                                oTable.fnDraw();
                                nEditing = null;
                            } else if (data.status == "false") {
                                alert(data.reason);
                            }
                        }
                    });
                } else {
                    oTable.fnUpdate(jqInputs[0].value, nRow, 0, false);
                    oTable.fnUpdate(jqInputs[1].value, nRow, 1, false);
                    oTable.fnUpdate(jqInputs[2].value, nRow, 2, false);
                    oTable.fnUpdate(jqInputs[3].value, nRow, 3, false);
                    oTable.fnUpdate('<a class="edit" href="">编辑</a>', nRow, 4, false);
                    oTable.fnUpdate('<a class="delete" href="">删除</a>', nRow, 5, false);
                    oTable.fnDraw();
                }
            }

            function cancelEditRow(oTable, nRow) {
                var jqInputs = $('input', nRow);
                oTable.fnUpdate(jqInputs[0].value, nRow, 0, false);
                oTable.fnUpdate(jqInputs[1].value, nRow, 1, false);
                oTable.fnUpdate(jqInputs[2].value, nRow, 2, false);
                oTable.fnUpdate(jqInputs[3].value, nRow, 3, false);
                oTable.fnUpdate('<a class="edit" href="">编辑</a>', nRow, 4, false);
                oTable.fnDraw();
            }

            var oTable = $('#editable-sample').dataTable({
                "aLengthMenu": [
                    [5, 15, 20, -1],
                    [5, 15, 20, "All"] // change per page values here
                ],
                // set the initial value
                "iDisplayLength": 5,
                "sDom": "<'row'<'col-lg-6'l><'col-lg-6'f>r>t<'row'<'col-lg-6'i><'col-lg-6'p>>",
                "sPaginationType": "bootstrap",
                "oLanguage": {
                    "sLengthMenu": "_MENU_ 每页记录数",
                    "oPaginate": {
                        "sPrevious": "上一页",
                        "sNext": "下一页"
                    }
                },
                "aoColumnDefs": [{
                    'bSortable': false,
                    'aTargets': [0]
                }
                ]
            });

            jQuery('#editable-sample_wrapper .dataTables_filter input').addClass("form-control medium"); // modify table search input
            jQuery('#editable-sample_wrapper .dataTables_length select').addClass("form-control xsmall"); // modify table per page dropdown

            var nEditing = null;

            $('#editable-sample_new').click(function (e) {
                var type = $("#type").text();
                e.preventDefault();
                var aiNew = oTable.fnAddData(['', '', '', '',
                    '<a class="edit" href="">编辑</a>', '<a class="cancel" data-mode="new" href="">取消</a>'
                ]);
                var nRow = oTable.fnGetNodes(aiNew[0]);
                editRow(oTable, nRow, type);
                nEditing = nRow;
            });

            $('#editable-sample a.delete').live('click', function (e) {
                e.preventDefault();
                if (confirm("确认删除?") == false) {
                    return;
                }
                var nRow = $(this).parents('tr')[0];
                var type = $("#type").text();

                if (type == "用户管理") {
                    var name = $(this).parent().parent().find("td:eq(1)").text();
                    var phone = $(this).parent().parent().find("td:eq(2)").text();
                    $.ajax({
                        url: "/deleteUser",
                        data: {"name": name, "phone": phone},
                        type: "post",
                        success: function (data) {
                            if (data.status == "true") {
                                alert(data.reason);
                                oTable.fnDeleteRow(nRow);
                            } else if (data.status == "false") {
                                alert(data.reason);
                            }
                        }
                    });
                } else if (type == "器材管理") {
                    var itemName = $(this).parent().parent().find("td:eq(1)").text();
                    var area = $(this).parent().parent().find("td:eq(2)").text();
                    $.ajax({
                        url: "/deleteItem",
                        data: {"itemName": itemName, "area": area},
                        type: "post",
                        success: function (data) {
                            if (data.status == "true") {
                                alert(data.reason);
                                oTable.fnDeleteRow(nRow);
                            } else if (data.status == "false") {
                                alert(data.reason);
                            }
                        }
                    });
                }
            });

            $('#editable-sample a.cancel').live('click', function (e) {
                e.preventDefault();
                if ($(this).attr("data-mode") == "new") {
                    var nRow = $(this).parents('tr')[0];
                    oTable.fnDeleteRow(nRow);
                } else {
                    restoreRow(oTable, nEditing);
                    nEditing = null;
                }
            });

            //
            $('#editable-sample a.edit').live('click', function (e) {
                var type = $("#type").text();
                e.preventDefault();

                /* Get the row as a parent of the link that was clicked on */
                var nRow = $(this).parents('tr')[0];

                if (nEditing !== null && nEditing != nRow) {
                    /* Currently editing - but not this row - restore the old before continuing to edit mode */
                    restoreRow(oTable, nEditing);
                    editRow(oTable, nRow);
                    nEditing = nRow;
                } else if (nEditing == nRow && this.innerHTML == "保存") {
                    saveRow(oTable, nEditing, type);
                    nEditing = null;
                } else {
                    /* No edit in progress - let's start one */
                    editRow(oTable, nRow, type);
                    nEditing = nRow;
                }
            });
        }

    };

}();