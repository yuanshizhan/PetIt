# This imports all the layers for "poop" into poopLayers
poopLayers = Framer.Importer.load "imported/poop"

# This imports all the layers for "feed" into feedLayers
feedLayers = Framer.Importer.load "imported/feed"

# This imports all the layers for "Main" into mainLayers1
mainLayers = Framer.Importer.load "imported/Main"
	
for layerGroupName of mainLayers
	console.log(layerGroupName)
	window[layerGroupName] = mainLayers[layerGroupName]
	
for layerGroupName of feedLayers
	console.log(layerGroupName)
	window[layerGroupName] = feedLayers[layerGroupName]

eatingLayer = new Layer visible:false, width: 380, height: 380, image:"imported/eat.gif"
eatingLayer.x = -50
myImageLayer = new Layer
	x: 0
	y: 0
	width: 333
	height: 280
myImageLayer.image = "imported/playing.gif"
myImageLayer.visible=false

mainImage = new Layer
	x:0
	y:0
	width:333
	height:380
mainImage.image = "imported/Main.png"


happier.visible = false
Main_Group1.visible=true
Play_PlayGroup.visible=false
See_Playtime.visible=false
Running.visible=false
one.visible = false

fade = (layer) ->
	layer.animate
		properties: { opacity: 0 }
		curve: 'linear'
		
AddButton.on Events.Click, ->
	console.log("Add")
	one.visible = true
	zero.visible = false
	happier.visible = false
	
MinusButton.on Events.Click, ->
	console.log("Minus")
	one.visible = false
	zero.visible = true
	happier.visible = false

Main_PlayGroup.on Events.Click, ->
	console.log("Play")
	See_Playtime.visible=false
	Running.visible=false
	Play_PlayGroup.visible = true
	Main_Feed.visible = false
	Main_Group1.visible = false
	myImageLayer.visible=false
	one.visible=false
	zero.visible=false
	FeedButton.visible=false
	CancelButton.visible-false
	Main_PlayGroup.visible=false
	
FeedButton.on Events.Click, ->
	console.log("Feed")
	eatingLayer.visible = true
	
Play_PlayGroup.on Events.Click, ->
	myImageLayer.visible=true
	See_Playtime.visible=true
	Play_PlayGroup.visible = false
	Main_Feed.visible = false
	Main_Group1.visible = false
	
Main_Feed.on Events.Click, ->
	console.log("FEED")
	Main_Feed.visible = false
	Main_PlayGroup.visible = false
	Main_Group1.visible = false
	See_Playtime.visible=false

eatingLayer.on Events.Click, ->
	console.log("Main")
	Main_Feed.visible = true
	Main_Group1.visible = true
	Main_PlayGroup.visible = true
	eatingLayer.visible = false
	happier.visible = true
	
mainImage.visible = false

See_Playtime_Btn.on Events.Click, ->
	Running.visible=true
	myImageLayer.visible=false

Running.on Events.Click, ->
	See_Playtime.visible=true
	Running.visible = false
	Main_Feed.visible = true
	Main_Group1.visible = true
	Main_PlayGroup.visible = true
	MainLayer.visible = false
	mainImage.visible = true

See_Playtime_Btn_Back.on Events.Click, ->
	Main_Group1.visible=true
	See_Playtime.visible=false

