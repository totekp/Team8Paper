//This is the paper itself, it contains the all the textboxes that are to be displayed on screen at any given time
var paperData = (function() {
    function getTags() {
        return data.data.tags;
    }

    function getElements() {
        return data.data.elements;
    }

    function getElement(i) {
        return data.data.elements[i];
    }

    function getElementsLength() {
        return data.data.elements.length
    }

    function getElementByID(id) {
        for(var i = 0; i < getElementsLength(); i++)
        {
            var currElement = getElement(i);
            if(currElement._id == id) {
                return currElement;
            }
        }
    }

    function addElement(textBox) {
        data.data.elements[getElementsLength()] = JSON.parse(JSON.stringify(textBox));
    }

    function removeElement(id) {
        for(var i = 0; i < getElementsLength(); i++)
        {
            var currElement = getElement(i);
            if(currElement._id == id) {
                data.data.notes.elements(i, 1);
            }
        }

    }

    function getGroups() {
            return data.data.groups;
        }

    function getGroup(i) {
        return data.data.groups[i];
    }

    function getGroupsLength() {
        return data.data.groups.length
    }

    function getGroupByID(id) {
        for(var i = 0; i < getGroupsLength(); i++)
        {
            var currGroup = getGroup(i);
            if(currGroup._id == id) {
                return currGroup;
            }
        }
    }

    function addGroup(textBox) {
        data.data.groups[getGroupsLength()] = JSON.parse(JSON.stringify(textBox));
    }

    function removeGroup(id) {
        for(var i = 0; i < getGroupsLength(); i++)
        {
            var currGroup = getGroup(i);
            if(currGroup._id == id) {
                data.data.groups.splice(i, 1);
                return true;
            }
        }
        return false;
    }

    function getElement(i) {
        return data.data.elements[i];
    }

    function getUniqueElementId() {
        return "elementID_" +  getElementsLength();
    }

    function getElementsLength() {
        return data.data.elements.length;
    }

    function addElement(element) {
        data.data.elements[getElementsLength()] = JSON.parse(JSON.stringify(element));
    }

    function getElementByID(id) {
        for(var i = 0; i < getElementsLength(); i++)
        {
            var currElement = getElement(i);
            if(currElement._id == id) {
                return currElement;
            }
        }
    }

    function removeElement(id) {
        for(var i = 0; i < getElementsLength(); i++)
        {
            var currElement = getElement(i);
            if(currElement._id == id) {
                data.data.elements.splice(i, 1);
                return true;
            }
        }
        return false;
    }

    function getElementsInGroup(id) {
        var currGroup = getGroupByID(id);
        return currGroup.elementIds;
    }

    function addElementToGroup(elementID, groupID) {
        console.log(groupID);
        var currGroup = getGroupByID(groupID);
        currGroup.elementIds[currGroup.elementIds.length] = elementID;
    }

    return {getTags : getTags, getElements : getElements, getElement : getElement, getElementsLength : getElementsLength,
    getElementByID : getElementByID, addElement : addElement, removeElement : removeElement,
    getGroups : getGroups, getGroup : getGroup, getGroupsLength : getGroupsLength,
    getGroupByID : getGroupByID, addGroup : addGroup, removeGroup : removeGroup,
    getUniqueElementId : getUniqueElementId, getElementsLength : getElementsLength, addElement: addElement,
    getElement : getElement, getElementByID : getElementByID, removeElement : removeElement, getElementsInGroup : getElementsInGroup,
    addElementToGroup : addElementToGroup};
})();


var paper = (function() {
    var boxID = paperData.getGroupsLength();
    var groupID = "groupID_";
    var selectedGroup = "";

    function addNote(event) {
        var textBoxCreated = textBox.createNewTextBox(event, groupID + boxID);
        //An easy way to do a clone of an object.
        paperData.addGroup(textBoxCreated);
        boxID++;
        updateJSON();
    }

    function updateNotePosition(ui, boxID) {
        var currentGroup = paperData.getGroupByID(boxID);
        currentGroup.x = ui.position.left;
        currentGroup.y = ui.position.top;
        updateJSON();
    }

    function updateNoteSize(ui, boxID) {
        var currentGroup = paperData.getGroupByID(boxID);
        currentGroup.width = ui.size.width;
        currentGroup.height = ui.size.height;
        updateJSON();
    }

    function removeNoteFromPaper(boxID) {
        var removed = paperData.removeGroup(boxID);
        $("#" + selectedGroup).remove();
        if (removed) {
            updateJSON();
        }
    }

    function initExistingNotes() {
        for(var i = 0; i< paperData.getGroupsLength(); i++)
        {
            textBox.loadTextBox(paperData.getGroup(i));
        }
    }

    function setSelectedGroup(group) {
        selectedGroup = group;
        $("#paper_toolbar").fadeIn(400);
    }

    $("#paper_remove_group").click(
        function(){
            if(selectedGroup != "") {
                removeNoteFromPaper(selectedGroup);
                selectedGroup = "";
                $("#paper_toolbar").fadeOut(400);
            }
     });

    return {addNote : addNote, updateNotePosition : updateNotePosition, updateNoteSize : updateNoteSize, removeNoteFromPaper : removeNoteFromPaper, initExistingNotes : initExistingNotes, setSelectedGroup : setSelectedGroup};
})();


var textBox = (function() {
    var textBoxVars = new Object();
    //textBoxVars = pageX, pageY, width, height, content

    function createTextBox(event, boxID, isNewBox) {
        $("body").append("<div id='" + boxID + "'></div>");

        var addedElement;
        if(isNewBox) {
            addedElement = addNewElement(event, "text");
        }
        else {
            addedElement = paperData.getElementsInGroup(boxID);
            addedElement = paperData.getElementByID(addedElement);
            console.log(addedElement);
        }

        $("#"+boxID).append("<div contenteditable class = 'text_element' data-name='" + addedElement._id + "' id= '" + addedElement._id + "'></div>");

        if(!isNewBox) {
            $("#"+addedElement._id).text(addedElement.data);
        }

        $('#'+boxID).addClass("textBox");
        $('#'+boxID).css("position", "absolute");

        //$('#'+boxID).append("<textarea class='textBoxTextArea'></textarea>");
        $('#'+boxID).append("<div class='closeButton'></div>");
        $('#'+boxID).append("<div class='dragButton'></div>");
        setTextAreaMax(boxID);

         (function(boxID) {
         $('#'+boxID).draggable({cursor: "crosshair", containment: "#paper_canvas", stop:
            function(event, ui) {
                    paper.updateNotePosition(ui, boxID);
                }
            });
        })(boxID);

        $('#'+boxID).resizable({ containment: "#paper_canvas", minHeight: 50, minWidth: 50 });
        (function(boxID) {
            $('#'+boxID).on("resize",
                function(event, ui) {
                    setTextAreaMax(boxID);
                    paper.updateNoteSize(ui, boxID);
                }
            );
        })(boxID);

        $('.textBox').mousedown(
            function(event) {
                console.log(this.id);
                paper.setSelectedGroup(this.id);
        });

        return addedElement;
    }

    function createNewTextBox(event, boxID) {
        var addedElement = createTextBox(event, boxID, true);
        $('#'+boxID).css({top: event.pageY, left: event.pageX});
        textBoxVars._id = boxID;
        textBoxVars.title = "";
        textBoxVars.x = event.pageX;
        textBoxVars.y = event.pageY;
        textBoxVars.width = $("#"+boxID).width();
        textBoxVars.height = $("#"+boxID).height();
        textBoxVars.elementIds = new Array();
        textBoxVars.elementIds[0] = addedElement._id;
        textBoxVars.created =  event.timeStamp;
        textBoxVars.lastUpdated = event.timeStamp;

        return textBoxVars;
    }

    function addNewElement(event, elementKind) {
        var newElement = new Object();
        newElement._id = paperData.getUniqueElementId();
        newElement.kind = elementKind;
        newElement.data = "";
        newElement.x = 0;
        newElement.y = 0;
        newElement.z = 0;
        newElement.width = 0;
        newElement.height = 0;
        newElement.created = event.timeStamp;
        newElement.lastUpdated = event.timeStamp;

        paperData.addElement(newElement);
        console.log(data.data);
        return newElement;
    }

    function loadTextBox(data) {
        createTextBox(event, data._id, false)
        $('#'+data._id).css({top: data.y, left: data.x, width: data.width, height: data.height});
    }

    function setTextAreaMax(boxID) {
        var AREA_MAX_FACTOR = 10;
        $('#'+boxID).children('.textBoxTextArea').css({'max-height' : $('#'+boxID).height()-AREA_MAX_FACTOR , 'max-width' : $('#'+boxID).width()-AREA_MAX_FACTOR});
    }

   return {createNewTextBox : createNewTextBox, loadTextBox : loadTextBox};
})();


$("#paper_title").text(data.data.title);
document.title = data.data.title;

document.addEventListener('keydown', function (event) {
  var esc = event.which == 27,
      nl = event.which == 13,
      el = event.target,
      input = el.nodeName != 'INPUT' && el.nodeName != 'TEXTAREA';


if (input) {
    if (esc) {
        // restore state
        document.execCommand('undo');
        el.blur();
    }
    else if (nl) {
        // save
        if(el.getAttribute('data-name') == 'title') {
            data.data.title = el.innerHTML;
        }
        else {
            var elementID = el.getAttribute('data-name');
            paperData.getElementByID(elementID).data = el.innerHTML;
            console.log(data);
        }
        updateJSON();

        el.blur();
        event.preventDefault();
        document.title = data.data.title;
        }
    }
}, true);

var currentSelection;

function resizeCanvas() {
    var RESIZE_FACTOR = 65;
    $('#paper_canvas').height($(window).height()-$("#paper_title").height() - RESIZE_FACTOR);
}

function bindCanvasClick() {
    $('#paper_canvas').click(function(event) {
        paper.addNote(event);
    });
}

function updateJSON() {
    $.ajax({
        url: window.location.toString(),
        data: JSON.stringify(data.data),
        contentType: 'application/json',
        type: 'post'
    });
}

function initCanvas() {
    //var btn = $.fn.button.noConflict() // reverts $.fn.button to jqueryui btn
    //$.fn.btn = btn // assigns bootstrap button functionality to $.fn.btn
    console.log(data);
    resizeCanvas();
    bindCanvasClick();
    initTags();
    paper.initExistingNotes();
}

function initTags() {
    var tags = paperData.getTags().join(',');
    $('#paper_tags_input_display').importTags(tags);
    $('#paper_tags_input_display').tagsInput({
        'width':'100%',
        'height': '47px',
        'minChars' : 3,
        'maxChars' : 20,
        'onAddTag':function(value){
            paperData.getTags().push(value);
            updateJSON();
        },
        'onRemoveTag':function(value){
            paperData.getTags().splice( $.inArray(value, paper.tags), 1 );
            updateJSON();
        },
    });
}

initCanvas();

$(window).resize(function() {
    resizeCanvas();
});