var papers;
var searchTagCache = [];
var offCanvasNavVisible = false;
var alphaNumRegx = /^[A-Za-z0-9 _.-]+$/;
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
    updatePapers();

    initTransitions();
    initBinds();
    initCanvasMenu();
    initDashboard();
});

//Transition functions

function initTransitions(){
    $("#cover_contents").hide().fadeIn(2100);

}

//Utility functions

function initBinds(){
    $('#btn_start').click(function(){
        $( '#off_canvas_nav' ).multilevelpushmenu( 'expand' );
        offCanvasNavVisible = true;
    });

    $('#paper_settings_submit').click(function(){
        var newTitle = $('#paper_settings_title_input').prop('value');
        var id = $('#paper_settings_id').attr('data-id');
        if(id == ''){
            alert('Please select a paper to edit.')
            return;
        }else if(newTitle.length <= 3){
            alert('Please input at least 4 characters long.');
            return;
        }
        var paper = getPaper(id);
        paper.title = newTitle;
        //Tags are automatically updated when added and removed. Nothing to change.
        $.ajax({
            url: '/paper/'+paper._id,
            data: JSON.stringify(paper),
            contentType: 'application/json',
            type: 'post'
        })
          .done(function(result){
            updateDashboardEntry(paper._id);
          });
    });

    $('#paper_settings_duplicate').click(function(){
         var id = $('#paper_settings_id').attr('data-id');
         var paper = getPaper(id);

         $.ajax({
          type: 'POST',
          url: '/api1/duplicatePaper',
          data: JSON.stringify({_id:paper._id}),
          contentType: 'application/json; charset=utf-8'
        })
        .done(function(result){
            addDashboardEntry(result.data);
        });
    });

    $('#paper_settings_delete').click(function(){
        var id = $('#paper_settings_id').attr('data-id');
        var paper = getPaper(id);

        $.ajax({
          type: 'POST',
          url: '/api1/deletePaper',
          data: JSON.stringify({_id:paper._id}),
          contentType: 'application/json; charset=utf-8'
        })
        .done(function(result){
            removeDashboardEntry(paper._id);
        });
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
        title:'Home',
    });

    $('#nav_search').tooltip({
        placement:'right',
        title:'Search papers',
    });

    $('#nav_dashboard').tooltip({
        placement:'right',
        title:'Dashboard',
    });

    $('#search_tag_input').tagsInput({
        'width':'600px',
        'height': '47px',
        'defaultText': 'Search papers by tags',
        'minChars' : 3,
        'maxChars' : 20,
        'tagClass' : "thumbnail",
        'onAddTag':function(value){
            searchTagCache.push(value);
        },
        'onRemoveTag':function(value){
            searchTagCache.splice( $.inArray(value, searchTagCache), 1 );
        },
    });

    $('#search_tag_submit').click(function(){
        if(searchTagCache.length>0){
            $.ajax({
              type: 'POST',
              url: '/api1/searchTags',
              data: JSON.stringify({tags:searchTagCache}),
              contentType: 'application/json; charset=utf-8'
            })
              .done(function(result){
                    if(result.status=="success"){
                        $('#search_results').empty();
                        if(result.data.length){
                            for(var i=0;i<result.data.length;i++){
                                $('#search_results').append('<a href="/paper/'+result.data[i]._id+'" '+'class="btn btn-default" style="width:700px;text-align:left;">'+result.data[i].title+'</a>');
                            }
                        }else{
                            $('#search_results').append('<p style="color:red">No matching papers found</p>');
                        }

                        $('#search_result_container').slideDown();
                        //location.reload(); //very crude way of refreshing the view..
                    }else {
                        console.log("Paper search by tags request has failed.")
                    }
              });
        }
    });

    $('#paper_settings_tag_input').tagsInput({
        'width':'100%',
        'height': '200px',
        'minChars' : 3,
        'maxChars' : 20,
        'onAddTag':function(value){
            var paper = getPaper($('#paper_settings_id').attr('data-id'));
            paper.tags.push(value);
        },
        'onRemoveTag':function(value){
            var paper = getPaper($('#paper_settings_id').attr('data-id'));
            paper.tags.splice( $.inArray(value, paper.tags), 1 );
        },
    });

    $('#search_result_container').hide();

    $('#search_slide_up').click(function(){
        $('#search_result_container').slideUp();
    });
}

function removeDashboardEntry(id) {
    $("#thumbnail_"+id).fadeOut(500);
}

function addDashboardEntry(id) {
    updatePapers();
    addPaperToDash();
}

function updateDashboardEntry(id) {
    updatePapers();
    paper = getPaper(id);
    var data = $("#thumbnail_"+paper._id).children('div[id*="image_"]');

    var created = new Date(paper.created);
    var updated = new Date(paper.lastUpdated);
    data.html(
        '<i id='+
        paper._id+
        ' data-title="'+
        paper.title+
        '" data-created="'+
        created.toLocaleDateString()+
        '" data-updated="'+
        updated.toLocaleDateString()+
        '" data-tags="'+
        paper.tags.join(',')+
        '" class="fa fa-file-o fa-5x context-menu-one box menu-injected"></i>'
    );

    if(paper.title.length > 15) {
        $('#label_'+paper._id).html(paper.title.slice(0,12)+'...');
    }else{
        $('#label_'+paper._id).html(paper.title);
    }

    $('#date_'+paper._id).html(created.toLocaleDateString());
    $('#paper_settings_updated').html(updated.toLocaleDateString());
}

function updatePapers() {
    var response = $.getValues('/api1/recentPaperids'); //Non asynchronous request
        if(response.status!="success"){
            console.log("Failed to retrieve papers in non-async request");
        }
    papers = response.data;
}

function initCanvasMenu(){
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

    //Push items to off canvas menu
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
        addPaperToDash(papers[i]);
    }
}

//Helper functions

function addPaperToDash(paper){
    var created = new Date(paper.created);
    var updated = new Date(paper.lastUpdated);
    $('#paper_templates').append(dashPaperTemplate);
    $('#paper_template').attr('id','thumbnail_'+paper._id);
    $('#paper_image').append(
        '<i id='+
        paper._id+
        ' data-title="'+
        paper.title+
        '" data-created="'+
        created.toLocaleDateString()+
        '" data-updated="'+
        updated.toLocaleDateString()+
        '" data-tags="'+
        paper.tags.join(',')+
        '" class="fa fa-file-o fa-5x context-menu-one box menu-injected"></i>'
    );


    //Account for long titles to prevent screwing up each paper templates
    if(paper.title.length > 15) {
        $('#paper_label').append(paper.title.slice(0,12)+'...');
    }else{
        $('#paper_label').append(paper.title);
    }

    $('#paper_date').append(created.toLocaleDateString());
    $('#paper_open_btn').attr('href','/paper/'+paper._id);

    //Set the id of the current paper template to something else to avoid conflicts in the code above
    $('#paper_open_btn').attr('id','open_btn_'+paper._id);
    $('#paper_image').attr('id','image_'+paper._id);
    $('#paper_label').attr('id','label_'+paper._id);
    $('#paper_date').attr('id','date_'+paper._id);
    $('#thumbnail_'+paper._id).click(function (event) {
        handlePaperSelection(event);
    });
}

function handlePaperSelection(event) {
    var data = $("#"+event.currentTarget.id).children('div[id*="image_"]').children();
    var paper = {
        _id: data[0].id,
        title: $('#'+data[0].id).attr('data-title'),
        created: $('#'+data[0].id).attr('data-created'),
        lastUpdated: $('#'+data[0].id).attr('data-updated'),
        tags: $('#'+data[0].id).attr('data-tags')
    };

    $('#paper_settings_id').attr('data-id',paper._id);
    $('#paper_settings_title_input').attr('value',paper.title);
    $('#paper_settings_created').html(paper.created);
    $('#paper_settings_updated').html(paper.lastUpdated);
    $('#paper_settings_tag_input').importTags(paper.tags);
}

function getPaper(id){
    var i = 0;
    while (i<papers.length){
        if(papers[i]._id == id){
            return papers[i];
        }
        i++;
    }
}

function removePaper(id){
    var i = 0;
    while (i<papers.length){
        if(papers[i]._id == id){
            i = papers.length;
            papers.remove(i);
        }
        i++;
    }
}

//extensions

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