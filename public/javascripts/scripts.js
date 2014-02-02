//This is the paper itself, it contains the all the textboxes that are to be displayed on screen at any given time
var paper = (function() {
    if(data.data.notes === undefined) {
        data.data.notes = new Array();
    }
    var boxID = 0;
    var textBoxID = "textBoxID_";

    function addNote(event) {
        var textBoxCreated = textBox.createNewTextBox(event, textBoxID + boxID);
        //An easy way to do a clone of an object.
        data.data.notes[data.data.notes.length] = JSON.parse(JSON.stringify(textBoxCreated));
        console.log(JSON.stringify(data.data.notes));
        boxID++;
        $.ajax({
            url: window.location.toString(),
            data: JSON.stringify(data.data),
            contentType: 'application/json',
            type: 'post'
        });
        console.log(data.data);
    }

    function updateNotePosition(ui, boxID) {
        for(var i = 0; i< data.data.notes.length; i++)
        {
            if(data.data.notes[i].id == boxID) {
                data.data.notes[i].pageX = ui.position.left;
                data.data.notes[i].pageY = ui.position.top;
            }
        }
    }

    function updateNoteSize(ui, boxID) {
        for(var i = 0; i< data.data.notes.length; i++)
        {
            if(data.data.notes[i].id == boxID) {
                data.data.notes[i].width = ui.size.width;
                data.data.notes[i].height = ui.size.height;
            }
        }
    }

    function removeNoteFromPaper(boxID) {
        for(var i = 0; i< data.data.notes.length; i++)
        {
            if(data.data.notes[i].id == boxID) {
                data.data.notes.splice(i, 1);
            }
        }
    }

    function initExistingNotes() {
        for(var i = 0; i< data.data.notes.length; i++)
        {
            textBox.loadTextBox(data.data.notes[i]);
        }
    }
    return {addNote : addNote, updateNotePosition : updateNotePosition, updateNoteSize : updateNoteSize, removeNoteFromPaper : removeNoteFromPaper, initExistingNotes : initExistingNotes};
})();


var textBox = (function() {
    var textBoxVars = new Object();
    //textBoxVars = pageX, pageY, width, height, content

    function createTextBox(boxID) {
        $("body").append("<div id='" + boxID + "'></div>");

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
    }

    function createNewTextBox(event, boxID) {
        createTextBox(boxID);
        $('#'+boxID).css({top: event.pageY, left: event.pageX});
        textBoxVars.pageX = event.pageX;
        textBoxVars.pageY = event.pageY;
        textBoxVars.width = $('#'+boxID).width();
        textBoxVars.height = $('#'+boxID).height();
        textBoxVars.id = boxID;

        return textBoxVars;
    }

    function loadTextBox(data) {
        createTextBox(data.id)
        $('#'+boxID).css({top: data.pageY, left: data.pageX, width: data.width, height: data.height});
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
    } else if (nl) {
      // save
      console.log(data);
      console.log(window.location.toString());
      data.data[el.getAttribute('data-name')] = el.innerHTML;

      $.ajax({
        url: window.location.toString(),
        data: JSON.stringify(data.data),
        contentType: 'application/json',
        type: 'post'
      });


      el.blur();
      event.preventDefault();
      document.title = data.data.title;
    }
  }
}, true);

var currentSelection;

function resizeCanvas() {
    var RESIZE_FACTOR = 18;
    $('#paper_canvas').height($(window).height()-$("#paper_title").height() - RESIZE_FACTOR);
}

function bindCanvasClick() {
    $('#paper_canvas').click(function(event) {
        paper.addNote(event);
    });
}
function initCanvas() {
    console.log(data);
    resizeCanvas();
    bindCanvasClick();
    paper.initExistingNotes();
}

initCanvas();

$(window).resize(function() {
    resizeCanvas();
});