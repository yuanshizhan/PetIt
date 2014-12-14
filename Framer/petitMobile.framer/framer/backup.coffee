# This imports all the layers for "final_petit" into final_petitLayers
petitLayers = Framer.Importer.load "imported/final_petit"

for layerGroupName of petitLayers
	console.log(layerGroupName)
	window[layerGroupName] = petitLayers[layerGroupName]

SignUpButton.visible = true
SignUpPage.visible = true
EnterName.visible = false
home_page_one_pet.visible=false
J.visible=false
O.visible=false
H.visible=false
N.visible=false
EnterName.visible=false
new_pet.visible=false
k.visible = false
ki.visible = false
kik.visible = false
kiki.visible = false
home_page.visible = false
points_page.visible=false
Empty.visible=false

Continue.on Events.Click, ->
	home_page.visible=true
	

SignUpButton.on Events.Click, ->
	EnterName.visible = true
# 	Empty.visible=true
	SignUpButton.visible = false
	SignUpPage.visible = false
	Utils.delay 0.25, ->
		new_pet.visible=false
		home_page_one_pet.visible=false
		EnterName.visible=true
		J.visible=true
		EnterName.visible=true
		Utils.delay 0.25, ->
			O.visible=true
			EnterName.visible=true
			Utils.delay 0.25, ->
				H.visible=true
				EnterName.visible=true
				Utils.delay 0.25, ->
					N.visible=true
					EnterName.visible=true
					enter_name_done.visible=true
	
enter_name_done.on Events.Click, ->
	new_pet.visible = true
	all_but_select.visible = true
	EnterName.visible = false
	select_pet.visible = true
	
pusheen.on Events.Click, ->
	select_pet.visible = true
	Utils.delay 0.5, ->
	k.visible=true
	Utils.delay 0.25, ->
		ki.visible = true
		Utils.delay 0.25, ->
			kik.visible=true
			Utils.delay 0.25, ->
				kiki.visible = true

all_done.on Events.Click, ->
	new_pet.visible = false
	home_page_one_pet.visible = true

check_points_button.on Events.Click, ->
	points_page.visible = true
	home_page_one_pet.visible = false

back_button.on Events.Click, ->
	points_page.visible = false
	home_page.visible = true