/* global Alfresco, LogicECM, YAHOO */
if (typeof LogicECM === 'undefined' || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

(function() {
	LogicECM.module.ColorPicker = function(htmlId) {
		LogicECM.module.ColorPicker.superclass.constructor.call(this, 'LogicECM.module.ColorPicker', htmlId, null);

		return this;
	};

	YAHOO.lang.extend(LogicECM.module.ColorPicker, Alfresco.component.Base);

	YAHOO.lang.augmentObject(LogicECM.module.ColorPicker.prototype, {
		dir: Alfresco.constants.URL_RESCONTEXT + 'images/lecm-base/components/color-picker/', // location of jscolor directory (leave empty to autodetect)

		options: {
			binding: true, // automatic binding via <input class="...">
			preloading: true, // use image preloading?
			bindClass: 'color' // class name
		},
		install: function() {
			this.addEvent(window, 'load', this.init);
		},
		init: function() {
			if (this.options.binding) {
				this.bind();
			}
			if (this.options.preloading) {
				this.preload();
			}
		},
		getDir: function() {
			return this.dir;
		},
		bind: function() {
			var bindClass = this.options.bindClass;
			var matchClass = new RegExp('(^|\\s)(' + bindClass + ')\\s*(\\{[^}]*\\})?', 'i');
			var e = document.getElementsByTagName('input');
			for (var i = 0; i < e.length; i += 1) {
				var m;
				if (!e[i].color && e[i].className && (m = e[i].className.match(matchClass))) {
					var prop = {};
					if (m[3]) {
						try {
							prop = (new Function('return (' + m[3] + ')'))();
						} catch (eInvalidProp) {
						}
					}
					e[i].color = new this.color(e[i], prop, this);
				}
			}
		},
		preload: function() {
			for (var fn in this.imgRequire) {
				if (this.imgRequire.hasOwnProperty(fn)) {
					this.loadImage(fn);
				}
			}
		},
		images: {
			pad: [181, 101],
			sld: [16, 101],
			cross: [15, 15],
			arrow: [7, 11]
		},
		imgRequire: {},
		imgLoaded: {},
		requireImage: function(filename) {
			this.imgRequire[filename] = true;
		},
		loadImage: function(filename) {
			if (!this.imgLoaded[filename]) {
				this.imgLoaded[filename] = new Image();
				this.imgLoaded[filename].src = this.getDir() + filename;
			}
		},
		fetchElement: function(mixed) {
			return typeof mixed === 'string' ? document.getElementById(mixed) : mixed;
		},
		addEvent: function(el, evnt, func) {
			if (el.addEventListener) {
				el.addEventListener(evnt, func, false);
			} else if (el.attachEvent) {
				el.attachEvent('on' + evnt, func);
			}
		},
		fireEvent: function(el, evnt) {
			if (!el) {
				return;
			}
			if (document.createEvent) {
				var ev = document.createEvent('HTMLEvents');
				ev.initEvent(evnt, true, true);
				el.dispatchEvent(ev);
			} else if (document.createEventObject) {
				var ev = document.createEventObject();
				el.fireEvent('on' + evnt, ev);
			} else if (el['on' + evnt]) { // alternatively use the traditional event model (IE5)
				el['on' + evnt]();
			}
		},
		getElementPos: function(e) {
			var e1 = e, e2 = e;
			var x = 0, y = 0;
			if (e1.offsetParent) {
				do {
					x += e1.offsetLeft;
					y += e1.offsetTop;
				} while (e1 = e1.offsetParent);
			}
			while ((e2 = e2.parentNode) && e2.nodeName.toUpperCase() !== 'BODY') {
				x -= e2.scrollLeft;
				y -= e2.scrollTop;
			}
			return [x, y];
		},
		getElementSize: function(e) {
			return [e.offsetWidth, e.offsetHeight];
		},
		getRelMousePos: function(e) {
			var x = 0, y = 0;
			if (!e) {
				e = window.event;
			}
			if (typeof e.offsetX === 'number') {
				x = e.offsetX;
				y = e.offsetY;
			} else if (typeof e.layerX === 'number') {
				x = e.layerX;
				y = e.layerY;
			}
			return {x: x, y: y};
		},
		getViewPos: function() {
			if (typeof window.pageYOffset === 'number') {
				return [window.pageXOffset, window.pageYOffset];
			} else if (document.body && (document.body.scrollLeft || document.body.scrollTop)) {
				return [document.body.scrollLeft, document.body.scrollTop];
			} else if (document.documentElement && (document.documentElement.scrollLeft || document.documentElement.scrollTop)) {
				return [document.documentElement.scrollLeft, document.documentElement.scrollTop];
			} else {
				return [0, 0];
			}
		},
		getViewSize: function() {
			if (typeof window.innerWidth === 'number') {
				return [window.innerWidth, window.innerHeight];
			} else if (document.body && (document.body.clientWidth || document.body.clientHeight)) {
				return [document.body.clientWidth, document.body.clientHeight];
			} else if (document.documentElement && (document.documentElement.clientWidth || document.documentElement.clientHeight)) {
				return [document.documentElement.clientWidth, document.documentElement.clientHeight];
			} else {
				return [0, 0];
			}
		},
		/*
		 * Usage example:
		 * var myColor = new jscolor.color(myInputElement)
		 */

		color: function(target, prop, parent) {


			this.required = true; // refuse empty values?
			this.adjust = true; // adjust value to uniform notation?
			this.hash = true; // prefix color with # symbol?
			this.caps = true; // uppercase?
			this.slider = true; // show the value/saturation slider?
			this.valueElement = target; // value holder
			this.styleElement = target; // where to reflect current color
			this.onImmediateChange = null; // onchange callback (can be either string or function)
			this.hsv = [0, 0, 1]; // read-only  0-6, 0-1, 0-1
			this.rgb = [1, 1, 1]; // read-only  0-1, 0-1, 0-1
			this.minH = 0; // read-only  0-6
			this.maxH = 6; // read-only  0-6
			this.minS = 0; // read-only  0-1
			this.maxS = 1; // read-only  0-1
			this.minV = 0; // read-only  0-1
			this.maxV = 1; // read-only  0-1

			this.pickerOnfocus = true; // display picker on focus?
			this.pickerMode = 'HSV'; // HSV | HVS
			this.pickerPosition = 'bottom'; // left | right | top | bottom
			this.pickerSmartPosition = true; // automatically adjust picker position when necessary
			this.pickerButtonHeight = 20; // px
			this.pickerClosable = false;
			this.pickerCloseText = 'Close';
			this.pickerButtonColor = 'ButtonText'; // px
			this.pickerFace = 10; // px
			this.pickerFaceColor = 'ThreeDFace'; // CSS color
			this.pickerBorder = 1; // px
			this.pickerBorderColor = 'ThreeDHighlight ThreeDShadow ThreeDShadow ThreeDHighlight'; // CSS color
			this.pickerInset = 1; // px
			this.pickerInsetColor = 'ThreeDShadow ThreeDHighlight ThreeDHighlight ThreeDShadow'; // CSS color
			this.pickerZIndex = 100000000;


			for (var p in prop) {
				if (prop.hasOwnProperty(p)) {
					this[p] = prop[p];
				}
			}

			this.hidePicker = function() {
				if (isPickerOwner()) {
					removePicker();
				}
			};

			this.showPicker = function() {
				if (!isPickerOwner()) {
					var tp = parent.getElementPos(target); // target pos
					var ts = parent.getElementSize(target); // target size
					var vp = parent.getViewPos(); // view pos
					var vs = parent.getViewSize(); // view size
					var ps = getPickerDims(this); // picker size
					var a, b, c;
					switch (this.pickerPosition.toLowerCase()) {
						case 'left':
							a = 1;
							b = 0;
							c = -1;
							break;
						case 'right':
							a = 1;
							b = 0;
							c = 1;
							break;
						case 'top':
							a = 0;
							b = 1;
							c = -1;
							break;
						default:
							a = 0;
							b = 1;
							c = 1;
							break;
					}
					var l = (ts[b] + ps[b]) / 2;

					// picker pos
					if (!this.pickerSmartPosition) {
						var pp = [
							tp[a],
							tp[b] + ts[b] - l + l * c
						];
					} else {
						var pp = [
							-vp[a] + tp[a] + ps[a] > vs[a] ?
									(-vp[a] + tp[a] + ts[a] / 2 > vs[a] / 2 && tp[a] + ts[a] - ps[a] >= 0 ? tp[a] + ts[a] - ps[a] : tp[a]) :
									tp[a],
							-vp[b] + tp[b] + ts[b] + ps[b] - l + l * c > vs[b] ?
									(-vp[b] + tp[b] + ts[b] / 2 > vs[b] / 2 && tp[b] + ts[b] - l - l * c >= 0 ? tp[b] + ts[b] - l - l * c : tp[b] + ts[b] - l + l * c) :
									(tp[b] + ts[b] - l + l * c >= 0 ? tp[b] + ts[b] - l + l * c : tp[b] + ts[b] - l - l * c)
						];
					}
					drawPicker(pp[a], pp[b]);
				}
			};

			this.importColor = function() {
				if (!valueElement) {
					this.exportColor();
				} else {
					if (!this.adjust) {
						if (!this.fromString(valueElement.value, leaveValue)) {
							styleElement.style.backgroundImage = styleElement.jscStyle.backgroundImage;
							styleElement.style.backgroundColor = styleElement.jscStyle.backgroundColor;
							styleElement.style.color = styleElement.jscStyle.color;
							this.exportColor(leaveValue | leaveStyle);
						}
					} else if (!this.required && /^\s*$/.test(valueElement.value)) {
						valueElement.value = '';
						styleElement.style.backgroundImage = styleElement.jscStyle.backgroundImage;
						styleElement.style.backgroundColor = styleElement.jscStyle.backgroundColor;
						styleElement.style.color = styleElement.jscStyle.color;
						this.exportColor(leaveValue | leaveStyle);

					} else if (this.fromString(valueElement.value)) {
						// OK
					} else {
						this.exportColor();
					}
				}
			};

			this.exportColor = function(flags) {
				if (!(flags & leaveValue) && valueElement) {
					var value = this.toString();
					if (this.caps) {
						value = value.toUpperCase();
					}
					if (this.hash) {
						value = '#' + value;
					}
					valueElement.value = value;
					// added by vlevin to fire the action for validator
					YAHOO.util.UserAction.keyup(valueElement);
				}
				if (!(flags & leaveStyle) && styleElement) {
					styleElement.style.backgroundImage = "none";
					styleElement.style.backgroundColor =
							'#' + this.toString();
					styleElement.style.color =
							0.213 * this.rgb[0] +
							0.715 * this.rgb[1] +
							0.072 * this.rgb[2]
							< 0.5 ? '#FFF' : '#000';
				}
				if (!(flags & leavePad) && isPickerOwner()) {
					redrawPad();
				}
				if (!(flags & leaveSld) && isPickerOwner()) {
					redrawSld();
				}
			};

			this.fromHSV = function(h, s, v, flags) { // null = don't change
				if (h !== null) {
					h = Math.max(0.0, this.minH, Math.min(6.0, this.maxH, h));
				}
				if (s !== null) {
					s = Math.max(0.0, this.minS, Math.min(1.0, this.maxS, s));
				}
				if (v !== null) {
					v = Math.max(0.0, this.minV, Math.min(1.0, this.maxV, v));
				}

				this.rgb = HSV_RGB(
						h === null ? this.hsv[0] : (this.hsv[0] = h),
						s === null ? this.hsv[1] : (this.hsv[1] = s),
						v === null ? this.hsv[2] : (this.hsv[2] = v)
						);

				this.exportColor(flags);
			};

			this.fromRGB = function(r, g, b, flags) { // null = don't change
				if (r !== null) {
					r = Math.max(0.0, Math.min(1.0, r));
				}
				if (g !== null) {
					g = Math.max(0.0, Math.min(1.0, g));
				}
				if (b !== null) {
					b = Math.max(0.0, Math.min(1.0, b));
				}

				var hsv = RGB_HSV(
						r === null ? this.rgb[0] : r,
						g === null ? this.rgb[1] : g,
						b === null ? this.rgb[2] : b
						);
				if (hsv[0] !== null) {
					this.hsv[0] = Math.max(0.0, this.minH, Math.min(6.0, this.maxH, hsv[0]));
				}
				if (hsv[2] !== 0) {
					this.hsv[1] = hsv[1] === null ? null : Math.max(0.0, this.minS, Math.min(1.0, this.maxS, hsv[1]));
				}
				this.hsv[2] = hsv[2] === null ? null : Math.max(0.0, this.minV, Math.min(1.0, this.maxV, hsv[2]));

				// update RGB according to final HSV, as some values might be trimmed
				var rgb = HSV_RGB(this.hsv[0], this.hsv[1], this.hsv[2]);
				this.rgb[0] = rgb[0];
				this.rgb[1] = rgb[1];
				this.rgb[2] = rgb[2];

				this.exportColor(flags);
			};

			this.fromString = function(hex, flags) {
				var m = hex.match(/^\W*([0-9A-F]{3}([0-9A-F]{3})?)\W*$/i);
				if (!m) {
					return false;
				} else {
					if (m[1].length === 6) { // 6-char notation
						this.fromRGB(
								parseInt(m[1].substr(0, 2), 16) / 255,
								parseInt(m[1].substr(2, 2), 16) / 255,
								parseInt(m[1].substr(4, 2), 16) / 255,
								flags
								);
					} else { // 3-char notation
						this.fromRGB(
								parseInt(m[1].charAt(0) + m[1].charAt(0), 16) / 255,
								parseInt(m[1].charAt(1) + m[1].charAt(1), 16) / 255,
								parseInt(m[1].charAt(2) + m[1].charAt(2), 16) / 255,
								flags
								);
					}
					return true;
				}
			};

			this.toString = function() {
				return (
						(0x100 | Math.round(255 * this.rgb[0])).toString(16).substr(1) +
						(0x100 | Math.round(255 * this.rgb[1])).toString(16).substr(1) +
						(0x100 | Math.round(255 * this.rgb[2])).toString(16).substr(1)
						);
			};

			function RGB_HSV(r, g, b) {
				var n = Math.min(Math.min(r, g), b);
				var v = Math.max(Math.max(r, g), b);
				var m = v - n;
				if (m === 0) {
					return [null, 0, v];
				}
				var h = r === n ? 3 + (b - g) / m : (g === n ? 5 + (r - b) / m : 1 + (g - r) / m);
				return [h === 6 ? 0 : h, m / v, v];
			}

			function HSV_RGB(h, s, v) {
				if (h === null) {
					return [v, v, v];
				}
				var i = Math.floor(h);
				var f = i % 2 ? h - i : 1 - (h - i);
				var m = v * (1 - s);
				var n = v * (1 - s * f);
				switch (i) {
					case 6:
					case 0:
						return [v, n, m];
					case 1:
						return [n, v, m];
					case 2:
						return [m, v, n];
					case 3:
						return [m, n, v];
					case 4:
						return [n, m, v];
					case 5:
						return [v, m, n];
				}
			}

			function removePicker() {
				delete this.picker.owner;
				document.getElementsByTagName('body')[0].removeChild(this.picker.boxB);
			}

			function drawPicker(x, y) {
				if (!this.picker) {
					this.picker = {
						box: document.createElement('div'),
						boxB: document.createElement('div'),
						pad: document.createElement('div'),
						padB: document.createElement('div'),
						padM: document.createElement('div'),
						sld: document.createElement('div'),
						sldB: document.createElement('div'),
						sldM: document.createElement('div'),
						btn: document.createElement('div'),
						btnS: document.createElement('span'),
						btnT: document.createTextNode(THIS.pickerCloseText)
					};
					for (var i = 0, segSize = 4; i < parent.images.sld[1]; i += segSize) {
						var seg = document.createElement('div');
						seg.style.height = segSize + 'px';
						seg.style.fontSize = '1px';
						seg.style.lineHeight = '0';
						this.picker.sld.appendChild(seg);
					}
					this.picker.sldB.appendChild(this.picker.sld);
					this.picker.box.appendChild(this.picker.sldB);
					this.picker.box.appendChild(this.picker.sldM);
					this.picker.padB.appendChild(this.picker.pad);
					this.picker.box.appendChild(this.picker.padB);
					this.picker.box.appendChild(this.picker.padM);
					this.picker.btnS.appendChild(this.picker.btnT);
					this.picker.btn.appendChild(this.picker.btnS);
					this.picker.box.appendChild(this.picker.btn);
					this.picker.boxB.appendChild(this.picker.box);
				}

				var p = this.picker;

				// controls interaction
				p.box.onmouseup =
						p.box.onmouseout = function() {
							target.focus();
						};
				p.box.onmousedown = function() {
					abortBlur = true;
				};
				p.box.onmousemove = function(e) {
					if (holdPad || holdSld) {
						holdPad && setPad(e);
						holdSld && setSld(e);
						if (document.selection) {
							document.selection.empty();
						} else if (window.getSelection) {
							window.getSelection().removeAllRanges();
						}
						dispatchImmediateChange();
					}
				};
				p.padM.onmouseup =
						p.padM.onmouseout = function() {
							if (holdPad) {
								holdPad = false;
								parent.fireEvent(valueElement, 'change');
							}
						};
				p.padM.onmousedown = function(e) {
					// if the slider is at the bottom, move it up
					switch (modeID) {
						case 0:
							if (THIS.hsv[2] === 0) {
								THIS.fromHSV(null, null, 1.0);
							}
							;
							break;
						case 1:
							if (THIS.hsv[1] === 0) {
								THIS.fromHSV(null, 1.0, null);
							}
							;
							break;
					}
					holdPad = true;
					setPad(e);
					dispatchImmediateChange();
				};
				p.sldM.onmouseup =
						p.sldM.onmouseout = function() {
							if (holdSld) {
								holdSld = false;
								parent.fireEvent(valueElement, 'change');
							}
						};
				p.sldM.onmousedown = function(e) {
					holdSld = true;
					setSld(e);
					dispatchImmediateChange();
				};

				// picker
				var dims = getPickerDims(THIS);
				p.box.style.width = dims[0] + 'px';
				p.box.style.height = dims[1] + 'px';

				// picker border
				p.boxB.style.position = 'absolute';
				p.boxB.style.clear = 'both';
				p.boxB.style.left = x + 'px';
				p.boxB.style.top = y + 'px';
				p.boxB.style.zIndex = THIS.pickerZIndex;
				p.boxB.style.border = THIS.pickerBorder + 'px solid';
				p.boxB.style.borderColor = THIS.pickerBorderColor;
				p.boxB.style.background = THIS.pickerFaceColor;

				// pad image
				p.pad.style.width = parent.images.pad[0] + 'px';
				p.pad.style.height = parent.images.pad[1] + 'px';

				// pad border
				p.padB.style.position = 'absolute';
				p.padB.style.left = THIS.pickerFace + 'px';
				p.padB.style.top = THIS.pickerFace + 'px';
				p.padB.style.border = THIS.pickerInset + 'px solid';
				p.padB.style.borderColor = THIS.pickerInsetColor;

				// pad mouse area
				p.padM.style.position = 'absolute';
				p.padM.style.left = '0';
				p.padM.style.top = '0';
				p.padM.style.width = THIS.pickerFace + 2 * THIS.pickerInset + parent.images.pad[0] + parent.images.arrow[0] + 'px';
				p.padM.style.height = p.box.style.height;
				p.padM.style.cursor = 'crosshair';

				// slider image
				p.sld.style.overflow = 'hidden';
				p.sld.style.width = parent.images.sld[0] + 'px';
				p.sld.style.height = parent.images.sld[1] + 'px';

				// slider border
				p.sldB.style.display = THIS.slider ? 'block' : 'none';
				p.sldB.style.position = 'absolute';
				p.sldB.style.right = THIS.pickerFace + 'px';
				p.sldB.style.top = THIS.pickerFace + 'px';
				p.sldB.style.border = THIS.pickerInset + 'px solid';
				p.sldB.style.borderColor = THIS.pickerInsetColor;

				// slider mouse area
				p.sldM.style.display = THIS.slider ? 'block' : 'none';
				p.sldM.style.position = 'absolute';
				p.sldM.style.right = '0';
				p.sldM.style.top = '0';
				p.sldM.style.width = parent.images.sld[0] + parent.images.arrow[0] + THIS.pickerFace + 2 * THIS.pickerInset + 'px';
				p.sldM.style.height = p.box.style.height;
				try {
					p.sldM.style.cursor = 'pointer';
				} catch (eOldIE) {
					p.sldM.style.cursor = 'hand';
				}

				// "close" button
				function setBtnBorder() {
					var insetColors = THIS.pickerInsetColor.split(/\s+/);
					var pickerOutsetColor = insetColors.length < 2 ? insetColors[0] : insetColors[1] + ' ' + insetColors[0] + ' ' + insetColors[0] + ' ' + insetColors[1];
					p.btn.style.borderColor = pickerOutsetColor;
				}
				p.btn.style.display = THIS.pickerClosable ? 'block' : 'none';
				p.btn.style.position = 'absolute';
				p.btn.style.left = THIS.pickerFace + 'px';
				p.btn.style.bottom = THIS.pickerFace + 'px';
				p.btn.style.padding = '0 15px';
				p.btn.style.height = '18px';
				p.btn.style.border = THIS.pickerInset + 'px solid';
				setBtnBorder();
				p.btn.style.color = THIS.pickerButtonColor;
				p.btn.style.font = '12px sans-serif';
				p.btn.style.textAlign = 'center';
				try {
					p.btn.style.cursor = 'pointer';
				} catch (eOldIE) {
					p.btn.style.cursor = 'hand';
				}
				p.btn.onmousedown = function() {
					THIS.hidePicker();
				};
				p.btnS.style.lineHeight = p.btn.style.height;

				// load images in optimal order
				switch (modeID) {
					case 0:
						var padImg = 'hs.png';
						break;
					case 1:
						var padImg = 'hv.png';
						break;
				}
				p.padM.style.backgroundImage = "url('" + parent.getDir() + "cross.gif')";
				p.padM.style.backgroundRepeat = "no-repeat";
				p.sldM.style.backgroundImage = "url('" + parent.getDir() + "arrow.gif')";
				p.sldM.style.backgroundRepeat = "no-repeat";
				p.pad.style.backgroundImage = "url('" + parent.getDir() + padImg + "')";
				p.pad.style.backgroundRepeat = "no-repeat";
				p.pad.style.backgroundPosition = "0 0";

				// place pointers
				redrawPad();
				redrawSld();

				this.picker.owner = THIS;
				document.getElementsByTagName('body')[0].appendChild(p.boxB);
			}

			function getPickerDims(o) {
				var dims = [
					2 * o.pickerInset + 2 * o.pickerFace + parent.images.pad[0] +
							(o.slider ? 2 * o.pickerInset + 2 * parent.images.arrow[0] + parent.images.sld[0] : 0),
					o.pickerClosable ?
							4 * o.pickerInset + 3 * o.pickerFace + parent.images.pad[1] + o.pickerButtonHeight :
							2 * o.pickerInset + 2 * o.pickerFace + parent.images.pad[1]
				];
				return dims;
			}

			function redrawPad() {
				// redraw the pad pointer
				switch (modeID) {
					case 0:
						var yComponent = 1;
						break;
					case 1:
						var yComponent = 2;
						break;
				}
				var x = Math.round((THIS.hsv[0] / 6) * (parent.images.pad[0] - 1));
				var y = Math.round((1 - THIS.hsv[yComponent]) * (parent.images.pad[1] - 1));
				this.picker.padM.style.backgroundPosition =
						(THIS.pickerFace + THIS.pickerInset + x - Math.floor(parent.images.cross[0] / 2)) + 'px ' +
						(THIS.pickerFace + THIS.pickerInset + y - Math.floor(parent.images.cross[1] / 2)) + 'px';

				// redraw the slider image
				var seg = this.picker.sld.childNodes;

				switch (modeID) {
					case 0:
						var rgb = HSV_RGB(THIS.hsv[0], THIS.hsv[1], 1);
						for (var i = 0; i < seg.length; i += 1) {
							seg[i].style.backgroundColor = 'rgb(' +
									(rgb[0] * (1 - i / seg.length) * 100) + '%,' +
									(rgb[1] * (1 - i / seg.length) * 100) + '%,' +
									(rgb[2] * (1 - i / seg.length) * 100) + '%)';
						}
						break;
					case 1:
						var rgb, s, c = [THIS.hsv[2], 0, 0];
						var i = Math.floor(THIS.hsv[0]);
						var f = i % 2 ? THIS.hsv[0] - i : 1 - (THIS.hsv[0] - i);
						switch (i) {
							case 6:
							case 0:
								rgb = [0, 1, 2];
								break;
							case 1:
								rgb = [1, 0, 2];
								break;
							case 2:
								rgb = [2, 0, 1];
								break;
							case 3:
								rgb = [2, 1, 0];
								break;
							case 4:
								rgb = [1, 2, 0];
								break;
							case 5:
								rgb = [0, 2, 1];
								break;
						}
						for (var i = 0; i < seg.length; i += 1) {
							s = 1 - 1 / (seg.length - 1) * i;
							c[1] = c[0] * (1 - s * f);
							c[2] = c[0] * (1 - s);
							seg[i].style.backgroundColor = 'rgb(' +
									(c[rgb[0]] * 100) + '%,' +
									(c[rgb[1]] * 100) + '%,' +
									(c[rgb[2]] * 100) + '%)';
						}
						break;
				}
			}

			function redrawSld() {
				// redraw the slider pointer
				switch (modeID) {
					case 0:
						var yComponent = 2;
						break;
					case 1:
						var yComponent = 1;
						break;
				}
				var y = Math.round((1 - THIS.hsv[yComponent]) * (parent.images.sld[1] - 1));
				this.picker.sldM.style.backgroundPosition =
						'0 ' + (THIS.pickerFace + THIS.pickerInset + y - Math.floor(parent.images.arrow[1] / 2)) + 'px';
			}

			function isPickerOwner() {
				return this.picker && this.picker.owner === THIS;
			}

			function blurTarget() {
				if (valueElement === target) {
					THIS.importColor();
				}
				if (THIS.pickerOnfocus) {
					THIS.hidePicker();
				}
			}

			function blurValue() {
				if (valueElement !== target) {
					THIS.importColor();
				}
			}

			function setPad(e) {
				var mpos = parent.getRelMousePos(e);
				var x = mpos.x - THIS.pickerFace - THIS.pickerInset;
				var y = mpos.y - THIS.pickerFace - THIS.pickerInset;
				switch (modeID) {
					case 0:
						THIS.fromHSV(x * (6 / (parent.images.pad[0] - 1)), 1 - y / (parent.images.pad[1] - 1), null, leaveSld);
						break;
					case 1:
						THIS.fromHSV(x * (6 / (parent.images.pad[0] - 1)), null, 1 - y / (parent.images.pad[1] - 1), leaveSld);
						break;
				}
			}

			function setSld(e) {
				var mpos = parent.getRelMousePos(e);
				var y = mpos.y - THIS.pickerFace - THIS.pickerInset;
				switch (modeID) {
					case 0:
						THIS.fromHSV(null, null, 1 - y / (parent.images.sld[1] - 1), leavePad);
						break;
					case 1:
						THIS.fromHSV(null, 1 - y / (parent.images.sld[1] - 1), null, leavePad);
						break;
				}
			}

			function dispatchImmediateChange() {
				if (THIS.onImmediateChange) {
					var callback;
					if (typeof THIS.onImmediateChange === 'string') {
						callback = new Function(THIS.onImmediateChange);
					} else {
						callback = THIS.onImmediateChange;
					}
					callback.call(THIS);
				}
			}

			var THIS = this;
			var modeID = this.pickerMode.toLowerCase() === 'hvs' ? 1 : 0;
			var abortBlur = false;
			var valueElement = parent.fetchElement(this.valueElement),
					styleElement = parent.fetchElement(this.styleElement);
			var holdPad = false,
					holdSld = false;
			var leaveValue = 1 << 0,
					leaveStyle = 1 << 1,
					leavePad = 1 << 2,
					leaveSld = 1 << 3;

			// target
			parent.addEvent(target, 'focus', function() {
				if (THIS.pickerOnfocus) {
					THIS.showPicker();
				}
			});
			parent.addEvent(target, 'blur', function() {
				if (!abortBlur) {
					window.setTimeout(function() {
						abortBlur || blurTarget();
						abortBlur = false;
					}, 0);
				} else {
					abortBlur = false;
				}
			});

			// valueElement
			if (valueElement) {
				var updateField = function() {
					THIS.fromString(valueElement.value, leaveValue);
					dispatchImmediateChange();
				};
				parent.addEvent(valueElement, 'keyup', updateField);
				parent.addEvent(valueElement, 'input', updateField);
				parent.addEvent(valueElement, 'blur', blurValue);
				valueElement.setAttribute('autocomplete', 'off');
			}

			// styleElement
			if (styleElement) {
				styleElement.jscStyle = {
					backgroundImage: styleElement.style.backgroundImage,
					backgroundColor: styleElement.style.backgroundColor,
					color: styleElement.style.color
				};
			}

			// require images
			switch (modeID) {
				case 0:
					parent.requireImage('hs.png');
					break;
				case 1:
					parent.requireImage('hv.png');
					break;
			}
			parent.requireImage('cross.gif');
			parent.requireImage('arrow.gif');

			this.importColor();
		}

	}, true);
})();
