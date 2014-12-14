
feedLayers = Framer.Importer.load "imported/feed"

# This imports all the layers for "Main" into mainLayers1
mainLayers = Framer.Importer.load "imported/Main"



for feedLayerName of feedLayers
	console.log(feedLayerName)
	window[feedLayerName] = feedLayers[feedLayerName]

for layerGroupName of mainLayers
	console.log(layerGroupName)
	window[layerGroupName] = mainLayers[layerGroupName]

one.visible = false

layerArr = [Main_Feed, Main_Group1]

fade = (layer) ->
	layer.animate
		properties: { opacity: 0 }
		curve: 'linear'

show = (layer) ->
	layer.animate
		properties: { opacity: 100 }
		curve: 'linear'

Main_Feed.on Events.Click, ->
	console.log("FEED")
	Main_Feed.visible = false
	Main_Group1.visible = false
	
eatingLayer = new Layer visible:false, width:400, height:380, image:"imported/eat.gif"

eatingLayer.x = -50

CancelButton.on Events.Click, ->
	console.log("Cancel")
	Main_Feed.visible = true
	Main_Group1.visible = true

AddButton.on Events.Click, ->
	console.log("Add")
	one.visible = true
	zero.visible = false

MinusButton.on Events.Click, ->
	console.log("Minus")
	one.visible = false
	zero.visible = true
	
FeedButton.on Events.Click, ->
	console.log("Feed")
	eatingLayer.visible = true

eatingLayer.on Events.Click, ->
	console.log("Main")
	Main_Feed.visible = true
	Main_Group1.visible = true
	eatingLayer.visible = false