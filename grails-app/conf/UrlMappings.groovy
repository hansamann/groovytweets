class UrlMappings {
    static mappings = {
        "/$controller/$action?/$id?"{
	      constraints {
			 // apply constraints here
		  }
	  }
        "/"(controller:"public", action:'list50')
        "500"(view:'/error')
	}
}
