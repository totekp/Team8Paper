var papers;
var offCanvasNavVisible = false;
var dashPaperTemplate = "<div id='paper_template' style='display:inline-block;text-align:center;padding:15px;' class='thumbnail'>"+
                               "<div id='paper_image'></div>"+
                               "<div class='caption'>"+
                                 "<p id='paper_label'></p>"+
                                 "<p style='font-size:11px;' id='paper_date'></p>"+
                                 "<p><a href='#' style='width:100%' id='paper_open_btn' class='btn btn-primary' role='button'>Open</a></p>"+
                               "</div>"+
                             "</div>";
var arrMenu = [
  {
    title: 'Paper',
    id: 'menuID',
    icon: 'fa fa-columns',
    items: [
      {
        name: 'Create New Paper',
        icon: 'fa fa-plus',
        link: '/paper'
      },
      {
        name: 'Recent Papers',
        id: 'itemID',
        icon: 'fa fa-files-o',
        link: '',
        items: [
          {
            title: 'Recent Papers',
            icon: 'fa fa-files-o',
            items: []
          }
        ]
      },
      {
        name: 'Credit',
        icon: 'fa fa-lightbulb-o',
        link: ''
      }
    ]
  }
];

//Initialization
$(document).ready(function(){
    var response = $.getValues('/api1/recentPaperids'); //Non asynchronous get
    if(response.status!="success"){
        console.log("Failed to retrieve papers in non-async request");
    }
    papers = response.data;

    initBinds();
    initCanvasMenu();
    initDashboard();
    initDashContextMenu();
});



//Utility functions

function initBinds(){
    //Button click binds-----------------------------------
    $('#btn_start').click(function(){
        $( '#off_canvas_nav' ).multilevelpushmenu( 'expand' );
        offCanvasNavVisible = true;
    });

    $(window).resize(function () {
        $( '#off_canvas_nav' ).multilevelpushmenu( 'redraw' );
    });

    //Tooltip binds-----------------------------------
    $('#off_canvas_toggle').tooltip({
        placement:'right',
        title:'Paper quick options',
        html:true
    });

    $('#nav_home').tooltip({
        placement:'right',
        title:'To the top!',
    });

    $('#nav_search').tooltip({
        placement:'right',
        title:'Get dat Paper',
    });

    $('#nav_dashboard').tooltip({
        placement:'right',
        title:'Dashboard',
    });
}

function initCanvasMenu(){
    //off canvas navigation init-----------------------------------
    $('#off_canvas_nav').multilevelpushmenu({
        menu: arrMenu,
        containersToPush: [$('.pushobj')],
        menuWidth: '25%',
        menuHeight: '100%',
        collapsed:true,
        fullCollapse:true,
        preventItemClick:false
    });

    $( '#off_canvas_toggle' ).click(function(){
        if(offCanvasNavVisible){
            $( '#off_canvas_nav' ).multilevelpushmenu( 'collapse' );
            offCanvasNavVisible = false;
        }else{
            $( '#off_canvas_nav' ).multilevelpushmenu( 'expand' );
            offCanvasNavVisible = true;
        }
    });

    var itemsArray = [];
    var $addToMenu = $( '#off_canvas_nav' ).multilevelpushmenu( 'findmenusbytitle' , 'Recent Papers' ).first();

    for(var i=0;i<papers.length;i++){
        if(i<10){
            itemsArray.push({
                name: papers[i].title,
                icon: 'fa fa-pencil-square-o',
                link: '/paper/' + papers[i]._id + ''
            });
        }
    }
    $('#off_canvas_nav').multilevelpushmenu( 'additems' , itemsArray , $addToMenu , 0 );
}


function initDashboard(){

    for(var i=0;i<papers.length;i++){
        var created = new Date(papers[i].created);
        var updated = new Date(papers[i].lastUpdated);
        $('#paper_templates').append(dashPaperTemplate);
        $('#paper_template').attr('id','thumbnail_'+papers[i]._id);
        $('#paper_image').append(
            '<i id='+
            papers[i]._id+
            ' data-title="'+
            papers[i].title+
            '" data-created="'+
            created.toLocaleDateString()+
            '" data-updated="'+
            updated.toLocaleDateString()+
            '" data-tags="'+
            papers[i].tags+
            '" class="fa fa-file-o fa-5x context-menu-one box menu-injected"></i>'
        );
        $('#paper_label').append(papers[i].title);
        $('#paper_date').append(created.toLocaleDateString());
        $('#paper_open_btn').attr('href','/paper/'+papers[i]._id);

        //Set the id of the current paper template to something else to avoid conflicts in the code above
        $('#paper_open_btn').attr('id','open_btn_'+papers[i]._id);
        $('#paper_image').attr('id','image_'+papers[i]._id);
        $('#paper_label').attr('id','label_'+papers[i]._id);
        $('#paper_date').attr('id','date_'+papers[i]._id);
    }
}

function initDashContextMenu(){
    //Context menu binds-----------------------------------
    $.contextMenu({
        selector: '.context-menu-one',
        callback: function(key, options) {
            var paper = {
                id: options.$trigger[0].id,
                title: $('#'+options.$trigger[0].id).attr('data-title'),
                created: $('#'+options.$trigger[0].id).attr('data-created'),
                lastUpdated: $('#'+options.$trigger[0].id).attr('data-updated'),
                tags: $('#'+options.$trigger[0].id).attr('data-tags')
            };
            if(key=='edit'){
                $('#paper_settings_title').html(paper.title);
                $('#paper_settings_created').html(paper.created);
                $('#paper_settings_updated').html(paper.lastUpdated);
                $('#paper_settings_tags').html(paper.tags);
            }else if(key=='duplicate'){
                $.ajax({
                  type: 'POST',
                  url: '/api1/duplicatePaper',
                  data: JSON.stringify({_id:paper.id}),
                  contentType: 'application/json; charset=utf-8'
                })
                  .done(function(result){
                        if(result.status=="success"){
                            location.reload(); //very crude way of refreshing the view..
                        }
                  });
            }else if(key=='delete'){
                $.ajax({
                  type: 'POST',
                  url: '/api1/deletePaper',
                  data: JSON.stringify({_id:paper.id}),
                  contentType: 'application/json; charset=utf-8'
                })
                  .done(function(result){
                        if(result.status=="success"){
                            location.reload(); //very crude way of refreshing the view..
                        }
                  });
            }
        },
        items: {
            "edit": {name: "Edit", icon: "edit"},
            "sep1": "---------",
            "duplicate": {name: "Duplicate", icon: "copy"},
            "delete": {name: "Delete", icon: "delete"}
        }
    });
}



//jQuery extensions

jQuery.extend({
    getValues: function(url) {
        var result = null;
        $.ajax({
            url: url,
            type: 'get',
            async: false,
            success: function(data) {
                result = data;
            }
        });
       return result;
    }
});