$('#map').vectorMap({
    map: 'world_mill_en',
    regionStyle: {
        hover: {
            'fill-opacity': 0.6
        }
    },
    series: {
        regions: [
            {
                values: density,
                scale: ['#ffffff', '#330066'],
                normalizeFunction: 'polynomial'
            }
        ]
    },
    onRegionLabelShow: function (e, el, code) {
        var detailStr = "";

        var countryInfo = countryDetails[code];
        if (!countryInfo) {
            detailStr = "<br/>No information available";
        } else {
            for (var i in details) {
                var detail = details[i];
                detailStr += "<br/>";
                if (detail.style) {
                    detailStr += "<span style='" + detail.style + "'>"
                }
                detailStr += detail.title;
                detailStr += ": ";
                detailStr += countryInfo[detail.property];
                if (detail.style) {
                    detailStr += "</span>";
                }
            }
        }

        el.html("<span style='font-size: 16px'><b>" + el.html() + "</b></span>" + detailStr);
    }
});
