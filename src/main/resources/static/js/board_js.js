;(function($, window, document, undefined)
{
    var hasTouch = 'ontouchstart' in document;

    /**
     * Detect CSS pointer-events property
     * events are normally disabled on the dragging element to avoid conflicts
     * https://github.com/ausi/Feature-detection-technique-for-pointer-events/blob/master/modernizr-pointerevents.js
     */
    var hasPointerEvents = (function()
    {
        var el    = document.createElement('div'),
            docEl = document.documentElement;
        if (!('pointerEvents' in el.style)) {
            return false;
        }
        el.style.pointerEvents = 'auto';
        el.style.pointerEvents = 'x';
        docEl.appendChild(el);
        var supports = window.getComputedStyle && window.getComputedStyle(el, '').pointerEvents === 'auto';
        docEl.removeChild(el);
        return !!supports;
    })();

    var defaults = {
        listNodeName    : 'ol',
        itemNodeName    : 'li',
        rootClass       : 'dd',
        listClass       : 'dd-list',
        itemClass       : 'dd-item',
        dragClass       : 'dd-dragel',
        handleClass     : 'dd-handle',
        collapsedClass  : 'dd-collapsed',
        placeClass      : 'dd-placeholder',
        noDragClass     : 'dd-nodrag',
        emptyClass      : 'dd-empty',
        expandBtnHTML   : '<button data-action="expand" type="button">Expand</button>',
        collapseBtnHTML : '<button data-action="collapse" type="button">Collapse</button>',
        group           : 0,
        maxDepth        : 5,
        threshold       : 20
    };

    function Plugin(element, options)
    {
        this.w  = $(document);
        this.el = $(element);
        this.options = $.extend({}, defaults, options);
        this.init();
    }

    Plugin.prototype = {

        init: function()
        {
            var list = this;

            list.reset();

            list.el.data('nestable-group', this.options.group);

            list.placeEl = $('<div class="' + list.options.placeClass + '"/>');

            $.each(this.el.find(list.options.itemNodeName), function(k, el) {
                list.setParent($(el));
            });

            list.el.on('click', 'button', function(e) {
                if (list.dragEl) {
                    return;
                }
                var target = $(e.currentTarget),
                    action = target.data('action'),
                    item   = target.parent(list.options.itemNodeName);
                if (action === 'collapse') {
                    list.collapseItem(item);
                }
                if (action === 'expand') {
                    list.expandItem(item);
                }
            });

            var onStartEvent = function(e)
            {
                var handle = $(e.target);
                if (!handle.hasClass(list.options.handleClass)) {
                    if (handle.closest('.' + list.options.noDragClass).length) {
                        return;
                    }
                    handle = handle.closest('.' + list.options.handleClass);
                }

                if (!handle.length || list.dragEl) {
                    return;
                }

                list.isTouch = /^touch/.test(e.type);
                if (list.isTouch && e.touches.length !== 1) {
                    return;
                }

                e.preventDefault();
                list.dragStart(e.touches ? e.touches[0] : e);
            };

            var onMoveEvent = function(e)
            {
                if (list.dragEl) {
                    e.preventDefault();
                    list.dragMove(e.touches ? e.touches[0] : e);
                }
            };

            var onEndEvent = function(e)
            {
                if (list.dragEl) {
                    e.preventDefault();
                    list.dragStop(e.touches ? e.touches[0] : e);
                }
            };

            if (hasTouch) {
                list.el[0].addEventListener('touchstart', onStartEvent, false);
                window.addEventListener('touchmove', onMoveEvent, false);
                window.addEventListener('touchend', onEndEvent, false);
                window.addEventListener('touchcancel', onEndEvent, false);
            }

            list.el.on('mousedown', onStartEvent);
            list.w.on('mousemove', onMoveEvent);
            list.w.on('mouseup', onEndEvent);

        },

        serialize: function()
        {
            var data,
                depth = 0,
                list  = this;
            step  = function(level, depth)
            {
                var array = [ ],
                    items = level.children(list.options.itemNodeName);
                items.each(function()
                {
                    var li   = $(this),
                        item = $.extend({}, li.data()),
                        sub  = li.children(list.options.listNodeName);
                    if (sub.length) {
                        item.children = step(sub, depth + 1);
                    }
                    array.push(item);
                });
                return array;
            };
            data = step(list.el.find(list.options.listNodeName).first(), depth);
            return data;
        },

        serialise: function()
        {
            return this.serialize();
        },

        reset: function()
        {
            this.mouse = {
                offsetX   : 0,
                offsetY   : 0,
                startX    : 0,
                startY    : 0,
                lastX     : 0,
                lastY     : 0,
                nowX      : 0,
                nowY      : 0,
                distX     : 0,
                distY     : 0,
                dirAx     : 0,
                dirX      : 0,
                dirY      : 0,
                lastDirX  : 0,
                lastDirY  : 0,
                distAxX   : 0,
                distAxY   : 0
            };
            this.isTouch    = false;
            this.moving     = false;
            this.dragEl     = null;
            this.dragRootEl = null;
            this.dragDepth  = 0;
            this.hasNewRoot = false;
            this.pointEl    = null;
        },

        expandItem: function(li)
        {
            li.removeClass(this.options.collapsedClass);
            li.children('[data-action="expand"]').hide();
            li.children('[data-action="collapse"]').show();
            li.children(this.options.listNodeName).show();
        },

        collapseItem: function(li)
        {
            var lists = li.children(this.options.listNodeName);
            if (lists.length) {
                li.addClass(this.options.collapsedClass);
                li.children('[data-action="collapse"]').hide();
                li.children('[data-action="expand"]').show();
                li.children(this.options.listNodeName).hide();
            }
        },

        expandAll: function()
        {
            var list = this;
            list.el.find(list.options.itemNodeName).each(function() {
                list.expandItem($(this));
            });
        },

        collapseAll: function()
        {
            var list = this;
            list.el.find(list.options.itemNodeName).each(function() {
                list.collapseItem($(this));
            });
        },

        setParent: function(li)
        {
            if (li.children(this.options.listNodeName).length) {
                li.prepend($(this.options.expandBtnHTML));
                li.prepend($(this.options.collapseBtnHTML));
            }
            li.children('[data-action="expand"]').hide();
        },

        unsetParent: function(li)
        {
            li.removeClass(this.options.collapsedClass);
            li.children('[data-action]').remove();
            li.children(this.options.listNodeName).remove();
        },

        dragStart: function(e)
        {
            var mouse    = this.mouse,
                target   = $(e.target),
                dragItem = target.closest(this.options.itemNodeName);

            this.placeEl.css('height', dragItem.height());

            mouse.offsetX = e.offsetX !== undefined ? e.offsetX : e.pageX - target.offset().left;
            mouse.offsetY = e.offsetY !== undefined ? e.offsetY : e.pageY - target.offset().top;
            mouse.startX = mouse.lastX = e.pageX;
            mouse.startY = mouse.lastY = e.pageY;

            this.dragRootEl = this.el;

            this.dragEl = $(document.createElement(this.options.listNodeName)).addClass(this.options.listClass + ' ' + this.options.dragClass);
            this.dragEl.css('width', dragItem.width());

            dragItem.after(this.placeEl);
            dragItem[0].parentNode.removeChild(dragItem[0]);
            dragItem.appendTo(this.dragEl);

            $(document.body).append(this.dragEl);
            this.dragEl.css({
                'left' : e.pageX - mouse.offsetX,
                'top'  : e.pageY - mouse.offsetY
            });
            // total depth of dragging item
            var i, depth,
                items = this.dragEl.find(this.options.itemNodeName);
            for (i = 0; i < items.length; i++) {
                depth = $(items[i]).parents(this.options.listNodeName).length;
                if (depth > this.dragDepth) {
                    this.dragDepth = depth;
                }
            }
        },

        dragStop: function(e)
        {
            var el = this.dragEl.children(this.options.itemNodeName).first();
            el[0].parentNode.removeChild(el[0]);
            this.placeEl.replaceWith(el);

            this.dragEl.remove();
            this.el.trigger('change');
            if (this.hasNewRoot) {
                this.dragRootEl.trigger('change');
            }
            this.reset();
        },

        dragMove: function(e)
        {
            var list, parent, prev, next, depth,
                opt   = this.options,
                mouse = this.mouse;

            this.dragEl.css({
                'left' : e.pageX - mouse.offsetX,
                'top'  : e.pageY - mouse.offsetY
            });

            // mouse position last events
            mouse.lastX = mouse.nowX;
            mouse.lastY = mouse.nowY;
            // mouse position this events
            mouse.nowX  = e.pageX;
            mouse.nowY  = e.pageY;
            // distance mouse moved between events
            mouse.distX = mouse.nowX - mouse.lastX;
            mouse.distY = mouse.nowY - mouse.lastY;
            // direction mouse was moving
            mouse.lastDirX = mouse.dirX;
            mouse.lastDirY = mouse.dirY;
            // direction mouse is now moving (on both axis)
            mouse.dirX = mouse.distX === 0 ? 0 : mouse.distX > 0 ? 1 : -1;
            mouse.dirY = mouse.distY === 0 ? 0 : mouse.distY > 0 ? 1 : -1;
            // axis mouse is now moving on
            var newAx   = Math.abs(mouse.distX) > Math.abs(mouse.distY) ? 1 : 0;

            // do nothing on first move
            if (!mouse.moving) {
                mouse.dirAx  = newAx;
                mouse.moving = true;
                return;
            }

            // calc distance moved on this axis (and direction)
            if (mouse.dirAx !== newAx) {
                mouse.distAxX = 0;
                mouse.distAxY = 0;
            } else {
                mouse.distAxX += Math.abs(mouse.distX);
                if (mouse.dirX !== 0 && mouse.dirX !== mouse.lastDirX) {
                    mouse.distAxX = 0;
                }
                mouse.distAxY += Math.abs(mouse.distY);
                if (mouse.dirY !== 0 && mouse.dirY !== mouse.lastDirY) {
                    mouse.distAxY = 0;
                }
            }
            mouse.dirAx = newAx;

            /**
             * move horizontal
             */
            if (mouse.dirAx && mouse.distAxX >= opt.threshold) {
                // reset move distance on x-axis for new phase
                mouse.distAxX = 0;
                prev = this.placeEl.prev(opt.itemNodeName);
                // increase horizontal level if previous sibling exists and is not collapsed
                if (mouse.distX > 0 && prev.length && !prev.hasClass(opt.collapsedClass)) {
                    // cannot increase level when item above is collapsed
                    list = prev.find(opt.listNodeName).last();
                    // check if depth limit has reached
                    depth = this.placeEl.parents(opt.listNodeName).length;
                    if (depth + this.dragDepth <= opt.maxDepth) {
                        // create new sub-level if one doesn't exist
                        if (!list.length) {
                            list = $('<' + opt.listNodeName + '/>').addClass(opt.listClass);
                            list.append(this.placeEl);
                            prev.append(list);
                            this.setParent(prev);
                        } else {
                            // else append to next level up
                            list = prev.children(opt.listNodeName).last();
                            list.append(this.placeEl);
                        }
                    }
                }
                // decrease horizontal level
                if (mouse.distX < 0) {
                    // we can't decrease a level if an item preceeds the current one
                    next = this.placeEl.next(opt.itemNodeName);
                    if (!next.length) {
                        parent = this.placeEl.parent();
                        this.placeEl.closest(opt.itemNodeName).after(this.placeEl);
                        if (!parent.children().length) {
                            this.unsetParent(parent.parent());
                        }
                    }
                }
            }

            var isEmpty = false;

            // find list item under cursor
            if (!hasPointerEvents) {
                this.dragEl[0].style.visibility = 'hidden';
            }
            this.pointEl = $(document.elementFromPoint(e.pageX - document.body.scrollLeft, e.pageY - (window.pageYOffset || document.documentElement.scrollTop)));
            if (!hasPointerEvents) {
                this.dragEl[0].style.visibility = 'visible';
            }
            if (this.pointEl.hasClass(opt.handleClass)) {
                this.pointEl = this.pointEl.parent(opt.itemNodeName);
            }
            if (this.pointEl.hasClass(opt.emptyClass)) {
                isEmpty = true;
            }
            else if (!this.pointEl.length || !this.pointEl.hasClass(opt.itemClass)) {
                return;
            }

            // find parent list of item under cursor
            var pointElRoot = this.pointEl.closest('.' + opt.rootClass),
                isNewRoot   = this.dragRootEl.data('nestable-id') !== pointElRoot.data('nestable-id');

            /**
             * move vertical
             */
            if (!mouse.dirAx || isNewRoot || isEmpty) {
                // check if groups match if dragging over new root
                if (isNewRoot && opt.group !== pointElRoot.data('nestable-group')) {
                    return;
                }
                // check depth limit
                depth = this.dragDepth - 1 + this.pointEl.parents(opt.listNodeName).length;
                if (depth > opt.maxDepth) {
                    return;
                }
                var before = e.pageY < (this.pointEl.offset().top + this.pointEl.height() / 2);
                parent = this.placeEl.parent();
                // if empty create new list to replace empty placeholder
                if (isEmpty) {
                    list = $(document.createElement(opt.listNodeName)).addClass(opt.listClass);
                    list.append(this.placeEl);
                    this.pointEl.replaceWith(list);
                }
                else if (before) {
                    this.pointEl.before(this.placeEl);
                }
                else {
                    this.pointEl.after(this.placeEl);
                }
                if (!parent.children().length) {
                    this.unsetParent(parent.parent());
                }
                if (!this.dragRootEl.find(opt.itemNodeName).length) {
                    this.dragRootEl.append('<div class="' + opt.emptyClass + '"/>');
                }
                // parent root list has changed
                if (isNewRoot) {
                    this.dragRootEl = pointElRoot;
                    this.hasNewRoot = this.el[0] !== this.dragRootEl[0];
                }
            }
        }

    };

    $.fn.nestable = function(params)
    {
        var lists  = this,
            retval = this;

        lists.each(function()
        {
            var plugin = $(this).data("nestable");

            if (!plugin) {
                $(this).data("nestable", new Plugin(this, params));
                $(this).data("nestable-id", new Date().getTime());
            } else {
                if (typeof params === 'string' && typeof plugin[params] === 'function') {
                    retval = plugin[params]();
                }
            }
        });

        return retval || lists;
    };

})(window.jQuery || window.Zepto, window, document);
/*my scripts*/
$('.dd').nestable('serialize');
$('.viewlist').on('click', function() {
    $('ol.kanban').addClass('list')
    $('ol.list').removeClass('kanban')
    $('menu').addClass('list')
    $('menu').removeClass('kanban')
});
$('.viewkanban').on('click', function() {
    $('ol.list').addClass('kanban')
    $('ol.kanban').removeClass('list')
    $('menu').addClass('kanban')
    $('menu').removeClass('list')
});
/*colors*/
$('#color').spectrum({
    color: "#f00",
    change: function(color) {
        $("#label").text("change called: " + color.toHexString());
    }
});


document.querySelector('#p1').addEventListener('mdl-componentupgraded', function() {
    this.MaterialProgress.setProgress(44);
});

function goToMyPage() {
    window.location.href = '/';
}


// 보드 수정
function openBoardEditForm() {
    const boardContainer = document.querySelector('.board_container');
    const boardId = boardContainer.getAttribute('data-board-id');
    document.getElementById("BoardEditForm-" + boardId).style.display = "block";
}

function closeBoardEditForm() {
    const boardContainer = document.querySelector('.board_container');
    const boardId = boardContainer.getAttribute('data-board-id');
    document.getElementById("BoardEditForm-" + boardId).style.display = "none";
}

function submitBoardEditForm() {
    const boardContainer = document.querySelector('.board_container');
    const boardId = boardContainer.getAttribute('data-board-id');
    const boardName = document.getElementById("boardName-" + boardId).value;
    const boardDescription = document.getElementById("boardDescription-" + boardId).value;
    const boardColor = document.getElementById("boardColor-" + boardId).value;
    const participantsInput = document.getElementById("participants-" + boardId);
    let participants = [];

    if (participantsInput != null && participantsInput.value != null) {
        const inputValues = participantsInput.value.split(",");

        if (inputValues.length === 1) {
            participants.push(inputValues[0].trim());
        }

        participants = inputValues.map(participant => participant.trim());
    }

    const data = {
        boardName: boardName,
        boardDescription: boardDescription,
        boardColor: boardColor,
        participants: participants
    };

    fetch(`/api/users/boards/${boardId}`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(data)
    })
        .then(response => {
            if (response.ok) {
                return response.json();
            }
        })
        .then(data => {
            console.log('Success:', data);
            closeBoardEditForm();
            location.reload();
        })
        .catch(error => {
            console.error("Error updating board:", error);
            document.getElementById("editBoardFormErrorMessage").textContent = "보드 수정에 실패했습니다.";
        });
}


// 컬럼 생성
function openColumnAddForm() {
    document.getElementById("ColumnAddForm").style.display = "block";
}

function closeColumnAddForm() {
    document.getElementById("ColumnAddForm").style.display = "none";
}

function submitColumnAddForm() {
    const boardContainer = document.querySelector('.board_container');
    const boardId = boardContainer.getAttribute('data-board-id');

    const colName = document.getElementById("colName").value;
    const colIndex = document.getElementById("colIndex").value;

    const data = {
        colName: colName,
        colIndex: colIndex
    };

    fetch(`/api/users/boards/${boardId}/columns`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    })
        .then(response => {
            if (response.ok) {
                return response.json();
            }
        })
        .then(data => {
            console.log('Success creating column:', data);
            closeColumnAddForm();
            location.reload();
        })
        .catch((error) => {
            console.error('Error creating column:', error);
            document.getElementById("addColumnFormErrorMessage").textContent = "컬럼 생성에 실패했습니다.";
        });
}



// 컬럼 인덱스 수정
function openColIndexEditForm(button) {
    const columnId = $(button).attr("data-column-id");
    document.getElementById("ColIndexEditForm-" + columnId).style.display = "block";
}

function closeColIndexEditForm(button) {
    const columnId = $(button).attr("data-column-id");
    document.getElementById("ColIndexEditForm-" + columnId).style.display = "none";
}

function submitColIndexEditForm(button) {
    const columnId = $(button).attr("data-column-id");
    const colEditIndex = $("#colEditIndex-" + columnId).val();
    const boardId = $(".board_container").data("board-id");

    const data = {
        colIndex: colEditIndex
    };

    $.ajax({
        type: "PUT",
        url: `/api/users/boards/${boardId}/columns/${columnId}/${colEditIndex}`,
        contentType: "application/json",
        data: JSON.stringify(data),
        success: function (response) {
            console.log("Column updated successfully:", response);
            closeColIndexEditForm(button);
            location.reload();
        },
        error: function (error) {
            console.error("Error updating column:", error);
            document.getElementById("editColumnFormErrorMessage-" + columnId).textContent = "컬럼 수정에 실패했습니다.";
        }
    });
}


// 컬럼 이름 수정
function openColNameEditForm(button) {
    const columnId = $(button).attr("data-column-id");
    document.getElementById("ColNameEditForm-" + columnId).style.display = "block";
}

function closeColNameEditForm(button) {
    const columnId = $(button).attr("data-column-id");
    document.getElementById("ColNameEditForm-" + columnId).style.display = "none";
}

function submitColNameEditForm(button) {
    const columnId = $(button).attr("data-column-id");
    const colEditName = $("#colEditName-" + columnId).val();
    const boardId = $(".board_container").data("board-id");

    const data = {
        colName: colEditName
    };

    $.ajax({
        type: "PUT",
        url: `/api/users/boards/${boardId}/columns/${columnId}`,
        contentType: "application/json",
        data: JSON.stringify(data),
        success: function (response) {
            console.log("Column updated successfully:", response);
            closeColNameEditForm(button);
            location.reload();
        },
        error: function (error) {
            console.error("Error updating column:", error);
            document.getElementById("editColumnFormErrorMessage-" + columnId).textContent = "컬럼 수정에 실패했습니다.";
        }
    });
}

// 컬럼 삭제
function deleteColumn(button) {
    const columnId = $(button).attr("data-column-id");
    const boardId = $(".board_container").data("board-id");

    $.ajax({
        type: "DELETE",
        url: `/api/users/boards/${boardId}/columns/${columnId}`,
        contentType: "application/json",
        success: function (response) {
            console.log("Column deleted successfully:", response);
            alert('컬럼 삭제가 완료되었습니다.');
            location.reload();
        },
        error: function (xhr, status, error) {
            console.error("Error deleting column:", error);
            console.log("Server response:", xhr.responseText);

            alert("컬럼 삭제에 실패했습니다. 자세한 내용은 콘솔을 확인하세요.");
        }
    });
}


// 컬럼인덱스 순으로 정렬
$(document).ready(function() {
    const container = $(".flex_container");
    const items = container.find(".dd").toArray().sort(function(a, b) {
        const aIndex = parseInt($(a).find(".To-do").data("col-index"));
        const bIndex = parseInt($(b).find(".To-do").data("col-index"));
        return aIndex - bIndex;
    });
    container.empty().append(items);
});



// 카드 생성
function openCardAddForm(button) {
    const columnId = $(button).attr("data-column-id");
    document.getElementById("CardAddForm-" + columnId).style.display = "block";
}

function closeCardAddForm(button) {
    const columnId = $(button).attr("data-column-id");
    document.getElementById("CardAddForm-" + columnId).style.display = "none";
}

function submitCardAddForm(button) {
    const boardContainer = document.querySelector('.board_container');
    const boardId = boardContainer.getAttribute('data-board-id');
    const columnId = $(button).attr("data-column-id");
    const cardName = document.getElementById("cardName-" + columnId).value;
    const cardDescription = document.getElementById("cardDescription-" + columnId).value;
    const color = document.getElementById("color-" + columnId).value;
    const dueDate = document.getElementById("dueDate-" + columnId).value;
    const operatorInput = document.getElementById("operatorNicknames-" + columnId);
    let operatorNicknames = [];

    if (operatorInput != null && operatorInput.value != null) {
        const inputValues = operatorInput.value.split(",");

        if (inputValues.length === 1) {
            operatorNicknames.push(inputValues[0].trim());
        }

        operatorNicknames = inputValues.map(operatorNickname => operatorNickname.trim());
    }

    const data = {
        cardName: cardName,
        cardDescription: cardDescription,
        color: color,
        dueDate: dueDate,
        operatorNames: operatorNicknames
    };

    fetch(`/api/users/boards/${boardId}/columns/${columnId}/cards`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    })
        .then(response => {
            if (response.ok) {
                return response.json();
            }
        })
        .then(data => {
            console.log('Success creating card:', data);
            closeCardAddForm(button);
            location.reload();
        })
        .catch((error) => {
            console.error('Error creating card:', error);
            document.getElementById("addCardFormErrorMessage").textContent = "카드 생성에 실패했습니다.";
        });
}


// 카드 삭제
function deleteCard(button) {
    const cardId = $(button).attr("data-card-id");
    const columnId = $(button).attr("data-column-id");
    const boardId = $(".board_container").data("board-id");

    $.ajax({
        type: "DELETE",
        url: `/api/users/boards/${boardId}/columns/${columnId}/cards/${cardId}`,
        contentType: "application/json",
        success: function (response) {
            console.log("Card deleted successfully:", response);
            alert('카드 삭제가 완료되었습니다.');
            location.reload();
        },
        error: function (xhr, status, error) {
            console.error("Error deleting card:", error);
            console.log("Server response:", xhr.responseText);

            alert("카드 삭제에 실패했습니다. 자세한 내용은 콘솔을 확인하세요.");
        }
    });
}


// 카드 페이지로 이동
function viewCard(button) {
    const cardId = $(button).attr("data-card-id");
    const columnId = $(button).attr("data-column-id");
    const boardId = $(".board_container").data("board-id");

    window.location.href = `/users/boards/${boardId}/columns/${columnId}/cards/${cardId}`;
}


// 카드 이동
function moveCard(cardId, sourceColumnId, targetColumnId, newCardIndex) {
    const boardId = $(".board_container").data("board-id");

    const data = {
        newColIndex: targetColumnId,
        cardIndex: newCardIndex
    };

    $.ajax({
        type: "PUT",
        url: `/api/users/boards/${boardId}/columns/${sourceColumnId}/cards/${cardId}/col`,
        contentType: "application/json",
        data: JSON.stringify(data),
        success: function (response) {
            console.log("Card moved successfully:", response);
        },
        error: function (error) {
            console.error("Error moving card:", error);
            alert("카드 이동에 실패했습니다. 자세한 내용은 콘솔을 확인하세요.");
        }
    });
}

$(document).ready(function () {
    $(".kanban .cards").sortable({
        connectWith: ".kanban .cards",
        start: function (event, ui) {
            const originalColumnId = ui.item.closest('.kanban').attr("data-column-id");
            ui.item.data("originalColumnId", originalColumnId);
            ui.item.data("originalIndex", ui.item.index());
        },
        stop: function (event, ui) {
            const targetColumnId = ui.item.closest('.kanban').attr("data-column-id");
            const originalColumnId = ui.item.data("originalColumnId");
            const newIndex = ui.item.index();

            moveCard(ui.item.attr("data-card-id"), originalColumnId, targetColumnId, newIndex);
        },
    });
});

