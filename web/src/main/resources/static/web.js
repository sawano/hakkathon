/*
 * Copyright 2014 Daniel Sawano
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


function fetchData() {
    $.ajax({
        url: 'results',
        dataType: 'json',
        success: function (response) {
            populateTable(response);
        },
        complete: function () {
            setTimeout(fetchData, 2000);
        }
    });
}

function populateTable(scores) {
    try {
        $(function () {
            var $tbody = $('<tbody>');
            $.each(scores, function (i, item) {
                var $tr = $('<tr>').append(
                    $('<td>').text(i),
                    $('<td>').text(item.alias),
                    $('<td>').text(item.score)
                );
                $tbody.append($tr);
            });
            $('#scoresTable  tbody').replaceWith($tbody);
        });
    } catch (e) {
        console.log(e);
    }
}

function resetScoreboard() {
    $.ajax({
        url: 'reset',
        dataType: 'json',
        success: function (response) {
            $('#reset-button').popover('show');
            setTimeout(hidePopover, 2000);
        }
    })
}

function hidePopover() {
    $('#reset-button').popover('hide');
}
