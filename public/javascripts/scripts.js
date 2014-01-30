//This is the paper itself, it contains the all the textboxes that are to be displayed on screen at any given time
var paper = (function() {
    var notes = new Array();
    var boxID = 0;
    var textBoxID = "textBoxID_";

    function addNote(event) {
        var textBoxCreated = textBox.createTextBox(event, textBoxID + boxID);
        //An easy way to do a clone of an object.
        notes[notes.length] = JSON.parse(JSON.stringify(textBoxCreated));
        console.log(JSON.stringify(notes));
        boxID++;

    }

    function updateNotePosition(ui, boxID) {
        for(var i = 0; i< notes.length; i++)
        {
            if(notes[i].id == boxID) {
                notes[i].pageX = ui.position.left;
                notes[i].pageY = ui.position.top;
            }
        }
    }

    function updateNoteSize(ui, boxID) {
        for(var i = 0; i< notes.length; i++)
        {
            if(notes[i].id == boxID) {
                notes[i].width = ui.size.width;
                notes[i].height = ui.size.height;
            }
        }
    }

    function removeNoteFromPaper(boxID) {
        for(var i = 0; i< notes.length; i++)
        {
            if(notes[i].id == boxID) {
                notes.splice(i, 1);
            }
        }
    }
    return {addNote : addNote, updateNotePosition : updateNotePosition, updateNoteSize : updateNoteSize, removeNoteFromPaper : removeNoteFromPaper};
})();


var textBox = (function() {
    var textBoxVars = new Object();
    //textBoxVars = pageX, pageY, width, height, content

    function createTextBox(event, boxID) {
        $("body").append("<div id='" + boxID + "'></div>");
        $('#'+boxID).css({top: event.pageY, left: event.pageX});

        $('#'+boxID).addClass("textBox");
        $('#'+boxID).css("position", "absolute");

        $('#'+boxID).append("<textarea class='textBoxTextArea'></textarea>");
        $('#'+boxID).append("<div class='closeButton'></div>");
        setTextAreaMax(boxID);

        (function(boxID) {
        $('#'+boxID).draggable({ cursor: "crosshair", containment: "#paper_canvas", stop:
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

        $('.textBox').hover(
            function() {
                if($('#'+boxID).find('.closeButton').children().size() == 0)
                {
                    $('#'+boxID).find('.closeButton').button({ icons: { primary: "ui-icon-closethick"}, text: false });
                }
                $(this).find('.closeButton').delay(1000).fadeIn(500);
            },
            function() {
                $(this).find('.closeButton').delay(1000).fadeOut(500);
        });

        $('.closeButton').click(
            function(){
                $(this).closest('.textBox').fadeOut(1000);
                paper.removeNoteFromPaper($(this).closest('.textBox').attr("id"));
        });


        textBoxVars.pageX = event.pageX;
        textBoxVars.pageY = event.pageY;
        textBoxVars.width = $('#'+boxID).width();
        textBoxVars.height = $('#'+boxID).height();
        textBoxVars.id = boxID;
        return textBoxVars;
    }

    function setTextAreaMax(boxID) {
        var AREA_MAX_FACTOR = 10;
        $('#'+boxID).children('.textBoxTextArea').css({'max-height' : $('#'+boxID).height()-AREA_MAX_FACTOR , 'max-width' : $('#'+boxID).width()-AREA_MAX_FACTOR});
    }

   return {createTextBox : createTextBox};
})();

var currentSelection;

function resizeCanvas() {
    var RESIZE_FACTOR = 18;
    $('#paper_canvas').width($(window).width()-RESIZE_FACTOR);
    $('#paper_canvas').height($(window).height()-RESIZE_FACTOR);
}

function bindCanvasClick() {
    $('#paper_canvas').click(function(event) {
        paper.addNote(event);
    });
}
function initCanvas() {
    resizeCanvas();
    bindCanvasClick();
}

initCanvas();

$(window).resize(function() {
    resizeCanvas();
});