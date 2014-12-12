(function () {

	if (Alfresco.widget.InsituEditor && Alfresco.widget.InsituEditor.tagEditor) {
		Alfresco.widget.InsituEditor.tagEditor.prototype._generateMarkup = function InsituEditor_tagEditor__generateMarkup()
		{
			// Reset the array of persisted tag nodeRefs...
			this.tagRefs = [];

			if (this.markupGenerated)
			{
				this._generateCurrentTagMarkup();
				return;
			}

			var eAutoCompleteWrapper = document.createElement("span"),
				eAutoComplete = document.createElement("div"),
				eSave = new Element(document.createElement("a"),
					{
						// ALF-19091 fixing.
						href: "javascript:void(0)",
						innerHTML: Alfresco.util.message("button.save")
					}),
				eCancel = new Element(document.createElement("a"),
					{
						href: "javascript:void(0)",
						innerHTML: Alfresco.util.message("button.cancel")
					});

			// Create a hidden input field - the value of this field is what will be used to update the
			// cm:taggable property of the document when the "Save" button is clicked.
			this.hiddenInput = document.createElement("input");
			YUIDom.setAttribute(this.hiddenInput, "type", "hidden");
			YUIDom.setAttribute(this.hiddenInput, "name", this.params.name);

			// Create a new input field for entering new tags (this will also allow the user to select tags from
			// an auto-complete list...
			this.newTagInput = document.createElement("input");
			YUIDom.setAttribute(this.newTagInput, "type", "text");

			// Add the new tag input field and the auto-complete drop-down DIV element to the auto-complete wrapper
			eAutoCompleteWrapper.appendChild(this.newTagInput);
			eAutoCompleteWrapper.appendChild(eAutoComplete);

			// Create a new edit box (this contains all tag spans, as well as the auto-complete enabled input field for
			// adding new tags)...
			var editBox = document.createElement("div");
			YUIDom.addClass(editBox, "inlineTagEdit"); // This class should make the span look like a text input box
			this.currentTags = document.createElement("span");
			editBox.appendChild(this.currentTags);
			editBox.appendChild(eAutoCompleteWrapper); // Add the auto-complete wrapper (this contains the input field for typing tags)

			// Add any previously applied tags to the edit box, updating the array of applied tag nodeRefs as we go...
			this._generateCurrentTagMarkup();

			// Set the current tags in the hidden field...
			YUIDom.setAttribute(this.hiddenInput, "value", this.tagRefs.toString());

			// Add the main edit box to the form (all the tags go in this box)
			this.elEditForm.appendChild(editBox);

			YUIDom.addClass(eAutoCompleteWrapper, "inlineTagEditAutoCompleteWrapper");
			YUIDom.addClass(eAutoComplete, "inlineTagEditAutoComplete");
			this.elEditForm.appendChild(this.hiddenInput);
			this.elEditForm.appendChild(eSave);
			this.elEditForm.appendChild(eCancel);

			/* ************************************************************************************
			 *
			 * This section of code deals with setting up the auto-complete widget for the new tag
			 * input field. We need to set up a data source for retrieving the existing tags and
			 * which we will need to filter on the client.
			 *
			 **************************************************************************************/
			var oDS = new YAHOO.util.XHRDataSource(Alfresco.constants.PROXY_URI + "api/forms/picker/category/workspace/SpacesStore/tag:tag-root/children?selectableType=cm:category&searchTerm=&size=100&aspect=cm:taggable&");
			oDS.responseType = YAHOO.util.XHRDataSource.TYPE_JSON;
			// This schema indicates where to find the tag name in the JSON response
			oDS.responseSchema =
				{
					resultsList: "data.items",
					fields: ["name", "nodeRef"]
				};
			this.tagAutoComplete = new YAHOO.widget.AutoComplete(this.newTagInput, eAutoComplete, oDS);
			this.tagAutoComplete.questionMark = false;     // Removes the question mark on the query string (this will be ignored anyway)
			this.tagAutoComplete.applyLocalFilter = true;  // Filter the results on the client
			this.tagAutoComplete.queryDelay = 0.1           // Throttle requests sent
			this.tagAutoComplete.animSpeed = 0.08;
			this.tagAutoComplete.itemSelectEvent.subscribe(function (type, args)
			{
				// If the user clicks on an entry in the list then apply the selected tag
				var tagName = args[2][0],
					nodeRef = args[2][1];
				this._applyTag(tagName, nodeRef);
				if (YUIDom.isAncestor(this.currentTags, this.newTagInput))
				{
					// We must have just finished editing a tag, therefore we need to move
					// the auto-complete box out of the current tags...
					YUIDom.insertAfter(this.newTagInput.parentNode, this.currentTags);
				}
			}, this, true);
			// Update the result filter to remove any results that have already been used...
			this.tagAutoComplete.dataReturnEvent.subscribe(function (type, args)
			{
				var results = args[2];
				for (i = 0, j = results.length; i < j; i++)
				{
					var currNodeRef = results[i].nodeRef;
					var index = Alfresco.util.arrayIndex(this.tagRefs, currNodeRef);
					if (index != -1)
					{
						results.splice(i, 1); // Remove the result because it's already been used
						i--;                  // Decrement the index because it's about to get incremented (this avoids skipping an entry)
						j--;                  // Decrement the target length, because the arrays got shorter
					}
				}
			}, this, true);


			/* **************************************************************************************
			 *
			 * This section of code deals with handling enter keypresses in the new tag input field.
			 * We need to capture ENTER keypresses and prevent the form being submitted, but instead
			 * make a request to create the tag provided and then add it to the hidden variable that
			 * will get submitted when the "Save" link is used.
			 *
			 ****************************************************************************************/
			var _this = this;
			Event.addListener(this.newTagInput, "keypress", function (e)
			{
				if (e.keyCode == 13 && this.value.length > 0)
				{
					Event.stopEvent(e); // Prevent the surrounding form from being submitted
					_this._createTag(this.value, false);
				}
			});

			// This section of code handles deleting configured tags through the use of the backspacce key....
			var _this = this;
			Event.addListener(this.newTagInput, "keydown", function (e)
			{
				if (e.keyCode == 8 && this.newTagInput.value.length == 0)
				{
					if (this._editingTagIndex != -1)
					{
						// If a tag is being edited then we just need to remove the tag and reset the input field
						this.tagRefs.splice(this._editingTagIndex, 1); // Remove the nodeRef, the tag span has already been removed
						YUIDom.insertAfter(this.newTagInput.parentNode, this.currentTags); // Return the auto-complete elements to their correct position
					}
					else if (!this._tagPrimedForDelete && this.currentTags.children.length > 0)
					{
						this._tagPrimedForDelete = true;
						var lastTag = YUIDom.getLastChild(this.currentTags);
						YUIDom.addClass(lastTag, "inlineTagEditTagPrimed");
						YUIDom.addClass(lastTag.children[1], "hidden");
						YUIDom.removeClass(lastTag.children[2], "hidden");
					}
					else
					{
						// The backspace key was used when there are no more characters to delete
						// so we need to delete the last tag...
						if (this.tagRefs.length > 0)
						{
							this.tagRefs.pop();
							YUIDom.setAttribute(this.hiddenInput, "value", this.tagRefs.toString());
							this.currentTags.removeChild(YUIDom.getLastChild(this.currentTags));
						}
						this._tagPrimedForDelete = false; // If we've deleted a tag then we're no longer primed for deletion...
					}
				}
				else if (this._tagPrimedForDelete == true)
				{
					// If any key other than backspace is pressed and the last tag has been primed for deletion
					// then we should put it back to the normal state...
					this._tagPrimedForDelete = false;
					if (this.currentTags.children.length > 0)
					{
						var lastTag = YUIDom.getLastChild(this.currentTags);
						YUIDom.removeClass(lastTag, "inlineTagEditTagPrimed");
						YUIDom.addClass(lastTag.children[2], "hidden");
						YUIDom.removeClass(lastTag.children[1], "hidden");
					}
				}
			}, this, true);

			Event.addListener(editBox, "click", function (e)
			{
				this.newTagInput.select();
			}, this, true);

			Event.addListener(this.newTagInput, "blur", function (e)
			{
				if (this.balloon)
				{
					this.suppressInputBoxFocus = true;
					this.balloon.hide();
				}
				this.suppressInputBoxFocus = false;
			}, this, true);

			eSave.on("click", function (e)
			{
				// Check to see if any characters need to be converted into a tag...
				if (this.newTagInput.value.length > 0)
				{
					this._createTag(this.newTagInput.value, true, e);
				}
				else
				{
					this.form._submitInvoked(e);
				}

			}, this, true);

			eCancel.on("click", function (e)
			{
				Event.stopEvent(e);
				this.inputBox.value = "";
				this.doHide(true);
			}, this, true);

			this.inputBox = this.newTagInput;

			// Key Listener for [Escape] to cancel
			this.keyListener = new KeyListener(this.inputBox,
				{
					keys: [KeyListener.KEY.ESCAPE]
				},
			{
				fn: function (id, keyEvent)
				{
					Event.stopEvent(keyEvent[1]);
					this.inputBox.value = "";
					this.doHide(true);
				},
				scope: this,
				correctScope: true
			});

			// Balloon UI for errors
			this.balloon = Alfresco.util.createBalloon(editBox);
			this.balloon.onClose.subscribe(function (e)
			{
				try
				{
					if (!this.suppressInputBoxFocus)
					{
						this.inputBox.focus();
					}
				}
				catch (e)
				{
				}
			}, this, true);

			// Register validation handlers
			var vals = this.params.validations;
			for (var i = 0, ii = vals.length; i < ii; i++)
			{
				this.form.addValidation(this.inputBox, vals[i].type, vals[i].args, vals[i].when, vals[i].message);
			}

			// Initialise the form
			this.form.init();
			this.markupGenerated = true;
		};
	}

})();