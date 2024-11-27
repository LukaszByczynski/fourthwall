{ pkgs ? import <nixpkgs> {}, pkgs_stable ? import <nixpkgs-stable> {} }:

let
in pkgs.mkShell rec {
  name = "fourthwall";
  buildInputs = [
    pkgs.zulu17
    pkgs.gradle
    pkgs.openapi-generator-cli
	];
}
