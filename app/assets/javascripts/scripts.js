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

    function doesElementExist(id) {
        for(var i = 0; i < getElementsLength(); i++)
        {
            var currElement = getElement(i);
            if(currElement._id == id) {
                return true;
            }
        }
        return false;
    }

    function removeElement(id) {
        for(var i = 0; i < getElementsLength(); i++)
        {
            var currElement = getElement(i);
            if(currElement._id == id) {
                data.data.elements(i, 1);
            }
        }
    }

    function purgePaper() {
        data.data.elements = [];
        data.data.groups = [];
        $("#paper_toolbar").fadeOut(400);
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

    function doesGroupExist(id) {
        for(var i = 0; i < getGroupsLength(); i++)
        {
            var currGroup = getGroup(i);
            if(currGroup._id == id) {
                return true;
            }
        }
        return false;
    }

    function addGroup(textBox) {
        if(!doesGroupExist(textBox._id)) {
            data.data.groups[getGroupsLength()] = JSON.parse(JSON.stringify(textBox));
        }
        else {
            console.log(textBox + "  not Added");
        }
    }

    function removeGroup(id) {
        for(var i = 0; i < getGroupsLength(); i++)
        {
            var currGroup = getGroup(i);
            if(currGroup._id == id) {
                for(var x = 0; x < currGroup.elementIds.length; x++) {
                    removeElement(currGroup.elementIds[x]);
                }
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
        updateJSON();
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
        var currGroup = getGroupByID(groupID);
        currGroup.elementIds[currGroup.elementIds.length] = elementID;
    }

    function getNextID() {
        if(getGroupsLength() > 0) {
            var nextID = getGroup(getGroupsLength()-1)._id.replace("groupID_", "");
            nextID++;
            return nextID;
        }
        return 0;

    }

    return {getTags : getTags, getElements : getElements, getElement : getElement, getElementsLength : getElementsLength,
    getElementByID : getElementByID, addElement : addElement, removeElement : removeElement,
    getGroups : getGroups, getGroup : getGroup, getGroupsLength : getGroupsLength,
    getGroupByID : getGroupByID, doesGroupExist : doesGroupExist, addGroup : addGroup, removeGroup : removeGroup,
    getUniqueElementId : getUniqueElementId, getElementsLength : getElementsLength, addElement: addElement,
    getElement : getElement, getElementByID : getElementByID, removeElement : removeElement, getElementsInGroup : getElementsInGroup,
    addElementToGroup : addElementToGroup, doesElementExist : doesElementExist, purgePaper : purgePaper, getNextID : getNextID };
})();


var paper = (function() {
    var boxID = paperData.getNextID();
    var groupID = "groupID_";
    var selectedGroup = "";
    var canCreateNewGroups = true;

    function addNote(event) {
        if(canCreateNewGroups) {
            var textBoxCreated = textBox.createNewTextBox(event, groupID + boxID);
            //An easy way to do a clone of an object.
            paperData.addGroup(textBoxCreated);
            boxID++;
            updateJSON();
        }
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
        $("#" + boxID).remove();
        if (removed) {
            updateJSON();
        }
    }

    function removeAllNotesFromPaper() {
        groups = paperData.getGroups();
        for(var i = 0; i < groups.length; i++) {
            currentGroup = groups[i]._id;
            $("#" + currentGroup).remove();
        }
        paperData.purgePaper();
        updateJSON();
    }

    function initExistingNotes() {
        for(var i = 0; i< paperData.getGroupsLength(); i++)
        {
            textBox.loadTextBox(paperData.getGroup(i));
        }
    }

    function setSelectedGroup(group) {

        if(selectedGroup != "") {
            $('#'+selectedGroup).removeClass("boxSelected");
        }
        selectedGroup = group;
        $('#'+selectedGroup).addClass("boxSelected");
        $("#paper_toolbar").fadeIn(400);
    }

    function toggleGroupCreation() {
        canCreateNewGroups = !canCreateNewGroups;
        return canCreateNewGroups;
    }

    function getSelectedGroup() {
        return selectedGroup;
    }

    function checkURL(url) {
        return((/\.(gif|jpg|jpeg|tiff|png)$/i).test(url));
    }
    $("#image-url-submit").click(function(){
        var imageURL = $('#image-url').val();
        if(checkURL(imageURL)) {
            addedElement = textBox.addNewElement(event, "image", imageURL);
            $("#"+selectedGroup).append("<img id= '" + addedElement._id +"'src='" + addedElement.data +"'style ='max-width: 100%'>");
            $('#insert-picture-url-modal').modal('hide');
            paperData.addElementToGroup(addedElement._id, selectedGroup);
        }
        else {
            $('#image-url-error').empty();
            $('#image-url-error').append("Invalid URL Entered");
            $('#image-url-error').slideDown();
            $('#image-url-error').delay(2400).slideUp();
        }
    });

    $("#paper_create_new_group").click(
        function() {
        if(toggleGroupCreation()) {
            $("#paper_create_new_group").children().removeClass("option_disabled");
            $("#paper_create_new_group").attr("title", "Click to disable creation of new groups");
        }
        else {
            $("#paper_create_new_group").children().addClass("option_disabled");
            $("#paper_create_new_group").attr("title", "Click to enable creation of new groups");
        }
    });

    $("#paper_delete_all_groups").click(
        function() {
            removeAllNotesFromPaper();
        }
    )

    $("#paper_remove_group").click(
        function(){
            if(selectedGroup != "") {
                removeNoteFromPaper(selectedGroup);
                selectedGroup = "";
                $("#paper_toolbar").fadeOut(400);
            }
     });
    $("#paper_draggable_group").click(
        function(){
            var isDraggable = $("#"+selectedGroup).draggable("option", "disabled");
            $("#"+selectedGroup).draggable("option", "disabled", !isDraggable);
            if(isDraggable) {
                $("#"+selectedGroup).removeClass("boxSelected");
                $("#"+selectedGroup).addClass("boxDraggable");
            }
            else {
                $("#"+selectedGroup).addClass("boxSelected");
                $("#"+selectedGroup).removeClass("boxDraggable");
            }
    });
    return {addNote : addNote, updateNotePosition : updateNotePosition, updateNoteSize : updateNoteSize,
    removeNoteFromPaper : removeNoteFromPaper, initExistingNotes : initExistingNotes, setSelectedGroup : setSelectedGroup,
    getSelectedGroup : getSelectedGroup};
})();


var textBox = (function() {
    var textBoxVars = new Object();
    //textBoxVars = pageX, pageY, width, height, content

    function createTextBox(event, boxID, isNewBox) {
        $("#paper_canvas").append("<div id='" + boxID + "'></div>");

        var addedElement;
        if(isNewBox) {
            addedElement = addNewElement(event, "text");
            $("#"+boxID).append("<textarea type='text' class = 'text_element form-control'  id= '" + addedElement._id + "' placeholder='Start Typing'></textarea>");
        }
        else {

            elementGroup = paperData.getElementsInGroup(boxID);
            for(var i = 0; i < elementGroup.length; i++) {
                var currentElement = paperData.getElementByID(elementGroup[i]);
                if(currentElement.kind == "text") {
                    $("#"+boxID).append("<textarea type='text' class = 'text_element form-control'  id= '" + currentElement._id + "' placeholder='Start Typing'></textarea>");
                    $("#"+currentElement._id).val(currentElement.data);
                }
                else if(currentElement.kind == "image") {
                    $("#"+boxID).append("<img id= '" + currentElement._id +"'src='"+ currentElement.data +"'style ='max-width: 100%'>");
                }
            }
        }

        $('#'+boxID).addClass("textBox");
        $('#'+boxID).css("position", "absolute");
        $('#'+boxID).css("opacity", 1);
        $('#'+boxID).click(function(e) {
            e.stopPropagation();
        });

        //$('#'+boxID).append("<textarea class='textBoxTextArea'></textarea>");
        $('#'+boxID).append("<div class='closeButton'></div>");
        $('#'+boxID).append("<div class='dragButton'></div>");
        setTextAreaMax(boxID);

         (function(boxID) {
         $('#'+boxID).draggable({disabled: "true", cursor: "crosshair", containment: "#paper_canvas", stop:
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
        textBoxVars.modified = event.timeStamp;

        return textBoxVars;
    }

    function addNewElement(event, elementKind, url) {
        var newElement = new Object();
        newElement._id = paperData.getUniqueElementId();
        newElement.kind = elementKind;
        if(elementKind == "image") {
            newElement.data = url;
        }
        else {
            newElement.data = "";
        }
        newElement.x = 0;
        newElement.y = 0;
        newElement.z = 0;
        newElement.width = 0;
        newElement.height = 0;
        newElement.created = event.timeStamp;
        newElement.modified = event.timeStamp;
        console.log(newElement);
        paperData.addElement(newElement);
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

   return {createNewTextBox : createNewTextBox, loadTextBox : loadTextBox, addNewElement : addNewElement};
})();


$("#paper_title").text(data.data.title);
document.title = data.data.title;

document.addEventListener('keydown', function (event) {
  var esc = event.which == 27,
      del = event.which == 46,
      nl = event.which == 13,
      el = event.target,
      input = el.nodeName != 'INPUT' && el.nodeName != 'TEXTAREA';
    if (input) {
        if (del && el.nodeName == 'BODY') {
            paper.removeNoteFromPaper(paper.getSelectedGroup());
            updateJSON();
        }
        if (esc) {
            // restore state
            document.execCommand('undo');
            el.blur();
        }
        else if(el.getAttribute('data-name') == 'title') {
            if(nl) {
                data.data.title = el.innerHTML;

                el.blur();
                event.preventDefault();
                document.title = data.data.title;

                updateJSON();
            }
        }
    }


}, true);

document.addEventListener('keyup', function (event) {
  var esc = event.which == 27,
      nl = event.which == 13,
      el = event.target,
      input = el.nodeName != 'INPUT';

    if (input) {
        var elementID = el.id;
        if (esc) {
            // restore state
            document.execCommand('undo');
            el.blur();
        }

        else if(paperData.doesElementExist(elementID)){
            paperData.getElementByID(elementID).data = $("#"+elementID).val();
            updateJSON();
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


var shouldUpdateJSON = false;
function updateJSON() {
    shouldUpdateJSON = true;
}

setInterval(function() {
    if(shouldUpdateJSON) {
        $.ajax({
                url: window.location.toString(),
                data: JSON.stringify(data.data),
                contentType: 'application/json',
                type: 'post'
        });
        shouldUpdateJSON = false;
    }
}, 1000);

function initCanvas() {
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
        'width':'99%',
        'height': '42px',
        'minChars' : 3,
        'maxChars' : 20,
        'onAddTag':function(value){
            paperData.getTags().push(value);
            updateJSON();
        },
        'onRemoveTag':function(value){
            paperData.getTags().splice( $.inArray(value, paper.tags), 1 );
            updateJSON();
        }
    });
}

initCanvas();

$(window).resize(function() {
    resizeCanvas();
});