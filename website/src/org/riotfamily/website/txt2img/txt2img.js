var RiotImageReplacement = Class.create();
RiotImageReplacement.prototype = {
	initialize: function(generatorUrl, pixelUrl, selectors) {
		this.selectors = selectors;
		this.generatorUrl = generatorUrl;
		this.useFilter = false;
		/*@cc_on
		/*@if (@_jscript_version < 5.7)
			this.useFilter = true;
		/*@end
		@*/
		this.pixelImage = new Image();
		this.pixelImage.src = pixelUrl;
		this.createHoverRules();
		Event.onDOMReady(this.insertImages.bind(this));
		if (window.riotEditCallbacks) {
			addRiotEditCallback(this.insertImages.bind(this));
		}
	},

	createHoverRules: function() {
		$A(document.styleSheets).each(function(sheet) {
			$A(sheet.rules || sheet.cssRules).each(function(rule) {
				if (rule.selectorText) {
					rule.selectorText.split(',').each(function(sel) {
						if (sel.include(':hover')) {
							if (rule.style.color) {
								var newSel = sel.replace(/:hover/, ' .txt2imgHover');
								var newStyle = 'color: ' + rule.style.color;
								if (sheet.insertRule) {
									sheet.insertRule(newSel + ' {' + newStyle + '}', sheet.cssRules.length);
								}
								else if (sheet.addRule) {
									sheet.addRule(newSel, newStyle);
								}
							}
						}
					});
				}
			});
		});
	},

	insertImages: function(el) {
		new CssMatcher(this.selectors, this.processElement.bind(this)).match(document.body);
	},

	processSelector: function(el, sel) {
		var elements = new Selector(sel).findElements(el);
		for (var i = 0, len = elements.length; i < len; i++) {
			this.processElement(elements[i], sel);
		}
	},

	processElement: function(el, sel) {
		el = Element.extend(el);
		if (el.down('img.replacement') || el.className == 'print-text') {
			return;
		}
		el.onedit = this.processElement.bind(this, el, sel);
		var text = el.innerHTML;
		text = text.strip().gsub(/<br\/?>/i, '\n').stripTags();
		if (text.length > 0) {
			var transform = el.getStyle('text-transform') || '';
			var width = 0;
			if (el.getStyle('display') == 'block') {
				width = el.offsetWidth - parseInt(el.getStyle('padding-left'))
						- parseInt(el.getStyle('padding-right'));
			}
	
			var color = el.getStyle('color');
	
			var hoverEl = document.createElement('span');
			hoverEl.className = 'txt2imgHover';
			el.appendChild(hoverEl);
			var hoverColor = Element.getStyle(hoverEl, 'color');
			Element.remove(hoverEl);
	
			var hover = null;
			if (hoverColor != color) {
				hover = new Image();
				hover.src = this.getImageUrl(text, transform, width, sel, hoverColor);
			}
	
			var img = new Image();
			img.src = this.getImageUrl(text, transform, width, sel, color);
			this.insertImage(el, img, hover);
		}
	},

	getImageUrl: function(text, transform, width, sel, color) {
		var url = this.generatorUrl;
		url += url.include('?') ? '&' : '?';
		return url + 'text=' + this.encode(text) + '&transform=' + transform
				+ '&width=' + width + '&selector=' + this.encode(sel)
				+ '&color=' + this.encode(color);
	},

	encode: function(s) {
		// We have to convert % characters because the AlphaImageLoader decodes
		// correctly encoded URIs and converts %23 back to # (and %26 back to &)
		// thereby corrupting the URL.
		return escape(encodeURIComponent(s).replace(/%/g, '@')).replace(/%/g, '@');
	},

	setImageSrc: function(el, src) {
		if (this.useFilter) {
			el.style.filter = "progid:DXImageTransform.Microsoft.AlphaImageLoader(src='"
				+ src + "', sizingMethod='scale')";
		}
		else {
			el.src = src;
		}
	},

	insertImage: function(el, image, hover) {
		var img;
		img = document.createElement('img');
		img.style.verticalAlign = 'top';
		if (this.useFilter) {
			img.src = this.pixelImage.src;
			if (image.width > 0) {
				img.style.width = image.width + 'px';
				img.style.height = image.height + 'px';
			}
			else {
				image.onload = function() {
					img.style.width = this.width + 'px';
					img.style.height = this.height + 'px';
				}
			}
		}
		this.setImageSrc(img, image.src);
		img.className = 'replacement';

		if (hover) {
			var a = el.up('a') || el;
			if (a._txt2ImgOver) a.stopObserving('mouseover', a._txt2ImgOver);
			if (a._txt2ImgOut) a.stopObserving('mouseout', a._txt2ImgOut);
			a._txt2ImgOver = this.setImageSrc.bind(this, img, hover.src);
			a._txt2ImgOut = this.setImageSrc.bind(this, img, image.src);
			a.observe('mouseover', a._txt2ImgOver);
			a.observe('mouseout', a._txt2ImgOut);
		}

		var printText = document.createElement("span");
		printText.style.display = 'none';
		printText.className = "print-text";
		printText.innerHTML = el.innerHTML;
		el.innerHTML = '';
		el.appendChild(img);
		el.appendChild(printText);
	}
}

if (!Event.onDOMReady) {
	Object.extend(Event, {
		_domReady: function() {
			if (arguments.callee.done) return;
			arguments.callee.done = true;
			if (this._timer) clearInterval(this._timer);
			for (var i = 0; i < this._readyCallbacks.length; i++) {
					this._readyCallbacks[i]();
				}
			this._readyCallbacks = null;
		},
		onDOMReady: function(f) {
			if (!this._readyCallbacks) {
				Event._readyCallbacks = [];
				var domReady = this._domReady.bind(this);
				if (document.addEventListener && !Prototype.Browser.WebKit) {
					document.addEventListener("DOMContentLoaded", domReady, false);
				}
				else {
				/*@cc_on @*/
					/*@if (@_win32)
						document.write("<script id=__ie_onload defer src=javascript:void(0)><\/script>");
						document.getElementById("__ie_onload").onreadystatechange = function() {
							if (this.readyState == "complete") domReady();
						};
					@else @*/
						if (Prototype.Browser.WebKit) {
							this._timer = setInterval(function() {
								if (/loaded|complete/.test(document.readyState)) domReady();
							}, 10);
						}
						else {
							Event.observe(window, 'load', domReady);
						}
					/*@end
				@*/
				}
			}
			Event._readyCallbacks.push(f);
		}
	});
}

var ElementMatcher = Class.create();
ElementMatcher.prototype = {
		initialize: function(s) {
				var m = /([^.#]*)#?([^.]*)\.?(.*)/.exec(s);
				this.tagName = m[1] != '' ? m[1].toUpperCase() : null;
				this.id = m[2] != '' ? m[2] : null;
				this.className = m[3] != '' ? m[3] : null;
				this.classNameRegExp = new RegExp("(^|\\s)" + this.className + "(\\s|$)");
		},

		match: function(el) {
				if (this.tagName && this.tagName != el.tagName) return false;
				if (this.id && this.id != el.id) return false;
				if (this.className && !this.checkClassName(el)) return false;
				return true;
		},

	checkClassName: function(el) {
		var c = el.className;
		if (c.length == 0) return false;
		return c == this.className || c.match(this.classNameRegExp);
	},

		inspect: function() {
				return this.tagName + '#' + this.id + '.' + this.className;
		}
};

var CssSelector = Class.create();
CssSelector.prototype = {
		initialize: function(sel) {
				this.text = sel;
				this.matchers = [];
				var part = sel.split(/\s+/);
				for (var i = 0; i < part.length; i++) {
						this.matchers.push(new ElementMatcher(part[i]));
				}
				this.level = 0;
				this.matcher = this.matchers[0];
				this.prev = [];
		},

		match: function(el) {
				if (this.matcher.match(el)) {
						if (this.el) {
								this.prev.push(this.el);
						}
						this.el = el;
						this.matcher = this.matchers[++this.level];
						return this.level == this.matchers.length;
				}
				return false;
		},

		leave: function(el) {
				if (el == this.el) {
						this.el = this.prev.pop();
						this.matcher = this.matchers[--this.level];
				}
		}
}

var CssMatcher = Class.create();
CssMatcher.prototype = {
		initialize: function(selectors, handler) {
				this.sel = selectors.collect(function(s) {return new CssSelector(s)});
				this.handler = handler;
		},
		match: function(el) {
				var matched = false;
				for (var i = 0; i < this.sel.length; i++) {
						if (this.sel[i].match(el)) {
								this.handler(el, this.sel[i].text);
								matched = true;
								break;
						}
				}
				if (!matched) {
					var child = el.firstChild;
					while (child) {
					while (child && child.nodeType != 1) child = child.nextSibling;
				if (child) {
									this.match(child);
									child = child.nextSibling;
							}
					}
				}
				for (var i = 0; i < this.sel.length; i++) {
						this.sel[i].leave(el);
				}
				return matched;
		}
}

