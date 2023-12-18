var nhiptim = document.getElementById('nhiptim');
var oxy = document.getElementById('oxy');
var cothe = document.getElementById('cothe');
var nhietdo = document.getElementById('nhietdo');
var doam = document.getElementById('doam');


setInterval(function () {
  fetch('http://localhost:8080/IOT/Sensor_Data?action=getData')
    .then(function (resolve) {
      return resolve.json();
    })
    .then(function (data) {
      updateCanvas(data);

    })
  function updateCanvas(data) {
    let bpm = data.bpm;
    let sp = data.sp;
    let body = data.body;
    let temp = data.temp;
    let humi = data.humi;
    
    nhiptim.textContent = bpm + "BPM";
    oxy.textContent = sp + "%";
    cothe.textContent = body + "°C";
    nhietdo.textContent = temp + "°C";
    doam.textContent = humi + "%";
    
  }
}, 3000);


