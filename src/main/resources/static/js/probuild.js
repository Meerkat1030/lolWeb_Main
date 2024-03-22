document.addEventListener('DOMContentLoaded', function () {
    var ddEle = document.querySelectorAll('.match-info');

    ddEle.forEach(function (ddEle) {
        var kills = parseFloat(ddEle.querySelector('#kills').textContent);
        var deaths = parseFloat(ddEle.querySelector('#deaths').textContent);
        var assists = parseFloat(ddEle.querySelector('#assists').textContent);
        var kdaResult = ddEle.querySelector('.kda-result');
        var kda = calculateKda(kills, deaths, assists);
        kdaResult.textContent = kda;
    });
});

// KDA를 계산하는 함수
function calculateKda(kills, deaths, assists) {
    if (deaths !== 0) {
        var kdaValue = (kills + assists) / deaths;
        return kdaValue.toFixed(2);
    } else {
        return (kills + assists) + "Perfect Kill";
    }
}

function search() {
    // 입력 상자에서 검색어 가져오기
    var searchText = document.getElementById('searchBox').value.toLowerCase().replace(/\s/g, '');

    // 모든 match 요소들을 가져와서 반복합니다.
    var matches = document.getElementsByClassName('match');
    for (let i = 0; i < matches.length; i++) {
        var match = matches[i];
        // match의 모든 match-box 요소들을 가져와서 반복합니다.
        var matchBoxes = match.getElementsByClassName('match-user-info');
        var matchFound = false; // 해당 match에 검색어와 일치하는 요소가 있는지 여부를 나타내는 플래그

        for (let j = 0; j < matchBoxes.length; j++) {
            var matchBox = matchBoxes[j];
            // 챔피언, 소환사 변수선언
            var summonerName = matchBox.querySelector('#summonerName');
            var championName = matchBox.querySelector('#championName');
            // 변수의 텍스트 내용
            var summonerNameText = summonerName.innerText.toLowerCase().replace(/\s/g, '');
            var championNameText = championName.innerText.toLowerCase().replace(/\s/g, '');

            // 검색어와 일치하는 항목이 있으면 해당 match를 표시하고 플래그를 true로 설정합니다.
            if (summonerNameText.indexOf(searchText) !== -1 || championNameText.indexOf(searchText) !== -1) {
                matchFound = true;
                // 검색한 단어 배경색 추가
                var ddEle = matchBox.getElementsByTagName('dd');
                for (let k = 0; k < ddEle.length; k++) {
                    var ddEles = ddEle[k];
                    var ddText = ddEles.innerText.toLowerCase();
                    var index = ddText.indexOf(searchText);
                    if (index !== -1) {
                        var highlightedText = '<span style="background-color: #0058f3;">' + ddText.substring(index, index + searchText.length) + '</span>';
                        var newText = ddText.substring(0, index) + highlightedText + ddText.substring(index + searchText.length);
                        ddEles.innerHTML = newText;
                    }
                }
                break; // 일치하는 요소를 찾았으므로 더 이상 반복할 필요가 없습니다.
            }
        }

        // 해당 match에 검색어와 일치하는 요소가 없으면 숨깁니다.
        if (!matchFound) {
            match.style.display = 'none';
        } else {
            match.style.display = 'block';
        }
    }

}
