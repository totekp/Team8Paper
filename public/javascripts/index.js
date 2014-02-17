var offCanvasNavVisible = false;

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
        name: 'Store',
        icon: 'fa fa-shopping-cart',
        link: '#',
        items: [
          {
            title: 'Store',
            icon: 'fa fa-shopping-cart',
            items: [
              {
                name: 'Clothes',
                icon: 'fa fa-tags',
                link: '#',
                items: [
                  {
                    title: 'Clothes',
                    icon: 'fa fa-tags',
                    items: [
                      {
                        name: 'Women\'s Clothing',
                        icon: 'fa fa-female',
                        link: '#',
                        items: [
                          {
                            title: 'Women\'s Clothing',
                            icon: 'fa fa-female',
                            items: [
                              {
                                name: 'Tops',
                                link: '#'
                              },
                              {
                                name: 'Dresses',
                                link: '#'
                              },
                              {
                                name: 'Trousers',
                                link: '#'
                              },
                              {
                                name: 'Shoes',
                                link: '#'
                              },
                              {
                                name: 'Sale',
                                link: '#'
                              }
                            ]
                          }
                        ]
                      },
                      {
                        name: 'Men\'s Clothing',
                        icon: 'fa fa-male',
                        link: '#',
                        items: [
                          {
                            title: 'Men\'s Clothing',
                            icon: 'fa fa-male',
                            items: [
                              {
                                name: 'Shirts',
                                link: '#'
                              },
                              {
                                name: 'Trousers',
                                link: '#'
                              },
                              {
                                name: 'Shoes',
                                link: '#'
                              },
                              {
                                name: 'Sale',
                                link: '#'
                              }
                            ]
                          }
                        ]
                      }
                    ]
                  }
                ]
              },
              {
                name: 'Jewelry',
                link: '#'
              },
              {
                name: 'Music',
                link: '#'
              },
              {
                name: 'Grocery',
                link: '#'
              }
            ]
          }
        ]
      },
      {
        name: 'Logout',
        icon: 'fa fa-sign-out',
        link: '#'
      },
      {
        name: 'Credit',
        icon: 'fa fa-lightbulb-o',
        link: '#'
      }
    ]
  }
];


$(document).ready(function(){

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


    //populate recent papers off canvas nav element and dashboard-----------------------------------
    var itemsArray = [];
    var $addToMenu = $( '#off_canvas_nav' ).multilevelpushmenu( 'findmenusbytitle' , 'Recent Papers' ).first();
    $.get("/api1/recentPaperids").done(function(data) {
        var i = 0;
        while (i < data.data.length) {
            if(data.status == "success") {
                if(i<10){
                    itemsArray.push({
                        name: data.data[i].title,
                        icon: 'fa fa-pencil-square-o',
                        link: '/paper/' + data.data[i]._id + ''
                    });
                }
                $('#paper_container').append(
                    '<i id='+
                    data.data[i]._id+
                    ' class="fa fa-file-o fa-5x context-menu-one box menu-injected"></i><span>'+
                    data.data[i].title+'</span>'
                );
            }
            i++;
        }

        if(data.status == "success"){
            $('#off_canvas_nav').multilevelpushmenu( 'additems' , itemsArray , $addToMenu , 0 );
        }
    });


    //Context menu binds-----------------------------------
    $.contextMenu({
        selector: '.context-menu-one',
        callback: function(key, options) {
            var m = "clicked: " + key;
        },
        items: {
            "edit": {name: "Edit", icon: "edit"},
            "cut": {name: "Cut", icon: "cut"},
            "copy": {name: "Copy", icon: "copy"},
            "paste": {name: "Paste", icon: "paste"},
            "delete": {name: "Delete", icon: "delete"},
            "sep1": "---------",
            "quit": {name: "Quit", icon: "quit"}
        }
    });

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

});


