
# This imports all the layers for "poop" into poopLayers
poopLayers = Framer.Importer.load "imported/poop"

for layerGroupName of poopLayers
	console.log(layerGroupName)
	window[layerGroupName] = poopLayers[layerGroupName]

myImageLayer = new Layer
	x: -30
	y: 0
	width: 333
	height: 380
myImageLayer.image = "imported/Thank_You.gif"
myImageLayer.visible=false


poop_main_2.visible=false
happier.visible=false

Utils.delay 10, ->
	poop_main_2.visible=true
	
poop_main.on Events.Click, ->
	poop_main_2.visible=false
	BackLayer.visible=true
# 	myImageLayer.visible=true
	Main_Group1.visible=false
	Main_Feed.visible=false
	Main_PlayGroup.visible=false
	happier.visible=false
	TopBar.visible=true
	Main_Group1.visible=true
	Main_Feed.visible=true
	Main_PlayGroup.visible=true
	happier.visible=true

myImageLayer.on Events.Click, ->
	myImageLayer.visible=false
	Main_Group1.visible=true
	Main_Feed.visible=true
	Main_PlayGroup.visible=true
	happier.visible=true
